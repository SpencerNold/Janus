#if defined(__APPLE__) && defined(__MACH__)
#include "firewall.h"

#include "action.h"
#include "protocol.h"
#include "../libutil/linkedlist.h"
#include "../libutil/executor.h"

#include <stdio.h>
#include <stdlib.h>
#include <arpa/inet.h>

#include <iostream>

void log_internal(const char* message) {
    std::cout << "[INTERNAL] " << message << std::flush;
}

const char* get_protocol(firewall_t* fw) {
    const char* proto = NULL;
    if (fw->protocol == TCP) {
        proto = "tcp";
    } else if (fw->protocol == UDP) {
        proto = "udp";
    }
    return proto;
}

void get_cidr(char* buf, size_t size, uint32_t network, uint32_t broadcast) {
    uint32_t hostmask = broadcast ^ network;
    uint32_t x = hostmask;
    int count = 0;
    while (x) {
        count += x & 1;
        x >>= 1;
    }
    int prefixLength = 32 - count;
    struct in_addr addr;
    addr.s_addr = htonl(network);
    snprintf(buf, size, "%s/%d", inet_ntoa(addr), prefixLength);
}

void create_default_deny_rule(firewall_t* fw, char* buf, size_t size) {
    snprintf(buf, size, "block in quick proto %s to any port %d", get_protocol(fw), fw->port);
}

void create_allow_rule(firewall_t* fw, char* buf, size_t size, uint32_t network, uint32_t broadcast) {
    char cidr[20];
    get_cidr(cidr, sizeof(cidr), network, broadcast);
    snprintf(buf, size, "pass in quick proto %s from %s to any port %d", get_protocol(fw), cidr, fw->port);
}

void create_deny_rule(firewall_t* fw, char* buf, size_t size, int network, int broadcast) {
    char cidr[20];
    get_cidr(cidr, sizeof(cidr), network, broadcast);
    snprintf(buf, size, "block in quick proto %s from %s to any port %d", get_protocol(fw), cidr, fw->port);
}

int init_pfctl() {
    log_internal("Enabling packet filter...\n");
    exec_streamable("sudo pfctl -q -e 2>&1", log_internal);
    // add anchor "me.spencernold.janus" to /etc/pf.conf
    FILE* file = fopen("/etc/pf.conf", "r");
    if (!file) {
        perror("Failed to open pf.conf");
        return 1;
    }
    char line[1024];
    int found = 0;
    while (fgets(line, sizeof(line), file)) {
        if (strstr(line, "anchor \"me.spencernold.janus\"")) {
            found = 1;
            break;
        }
    }
    fclose(file);
    if (!found) {
        file = fopen("/etc/pf.conf", "a");
        if (!file) {
            perror("Failed to append to pf.conf");
            return 1;
        }
        fprintf(file, "\n%s\n", "anchor \"me.spencernold.janus\"");
        fclose(file);
        log_internal("Anchor added to pf.conf\n");
        exec_streamable("pfctl -q -f /etc/pf.conf 2>&1", log_internal);
    } else {
        log_internal("Anchor already exists\n");
    }
    exec_streamable("sudo pfctl -q -a me.spencernold.janus -F all 2>&1", log_internal);
    log_internal("Packet filter enabled!\n");
    return 0;
}

firewall_t* fw_start(int protocol, int port, int action) {
    if (init_pfctl()) {
        return NULL;
    }
    firewall_t* fw = (firewall_t*) malloc(sizeof(firewall_t));
    if (fw == NULL) {
        return NULL;
    }
    linked_list_t* list = ll_new();
    if (list == NULL) {
        free(fw);
        return NULL;
    }
    fw->protocol = protocol;
    fw->port = port;
    fw->action = action;
    fw->handle = list;
    if (action == DENY || action == TARPIT) {
        char buf[256];
        create_default_deny_rule(fw, buf, sizeof(buf));
        if (ll_add_string(list, buf) != 0) {
            fw_stop(fw);
            return NULL;
        }
    }
    // else if (action == ALLOW) do nothing, as by default pf allows, rule only needed if default action is to deny or tarpit
    return fw;
}

int fw_write_rule(firewall_t* fw, int action, uint32_t network, uint32_t broadcast) {
    linked_list_t* list = (linked_list_t*) fw->handle;
    if (action == DENY || action == TARPIT) {
        char buf[256];
        create_deny_rule(fw, buf, sizeof(buf), network, broadcast);
        return ll_add_string(list, buf);
    } else if (action == ALLOW) {
        char buf[256];
        create_allow_rule(fw, buf, sizeof(buf), network, broadcast);
        return ll_add_string(list, buf);
    }
    return 37;
}

int fw_commit(firewall_t* fw) {
    log_internal("Committing to internal firewall...\n");
    linked_list_t* list = (linked_list_t*) fw->handle;
    if (list->size == 0) return 0;
    FILE* pf = popen("pfctl -a me.spencernold.janus -f - 2>&1", "w");
    if (pf == NULL) {
        return 1;
    }
    ll_node_t* node = list->tail;
    while (node) {
        fputs((char*)node->value, pf);
        fputc('\n', pf);
        // node->value is the rule
        node = node->prev;
    }
    pclose(pf);
    log_internal("Committed to internal firewall!\n");
    return 0;
}

void fw_stop(firewall_t* fw) {
    log_internal("Shutting down internal firewall...");
    exec_streamable("sudo pfctl -a me.spencernold.janus -F all 2>&1", log_internal);
    linked_list_t* list = (linked_list_t*) fw->handle;
    ll_destroy(list, free);
    free(fw);
    log_internal("Internal firewall shutdown!\n");
}

#endif