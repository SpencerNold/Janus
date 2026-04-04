#if defined(__APPLE__) && defined(__MACH__)
#include "pcap_impl.h"

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <pcap.h>

void (*raw_handler)(pcap_handle*, const unsigned char*, int) = NULL;

pcap_handle* pch_open() {
    pcap_if_t* devices;
    char errbuf[PCAP_ERRBUF_SIZE];
    if (pcap_findalldevs(&devices, errbuf) == -1) {
        perror(errbuf);
        return NULL;
    }
    if (devices == NULL) {
        perror("No network devices found!");
        return NULL;
    }
    // grab first network device
    pcap_handle* handle = pch_open(devices->name);
    pcap_freealldevs(devices);
    return handle;
}

pcap_handle* pch_open(char* interface) {
    pcap_handle* handle = (pcap_handle*) malloc(sizeof(pcap_handle));
    if (handle == NULL) {
        return NULL;
    }
    char* name = strdup(interface);
    if (name == NULL) {
        free(handle);
        return NULL;
    }
    char errbuf[PCAP_ERRBUF_SIZE];
    pcap_t* pcap = pcap_open_live(name, 65535, 1, 1000, errbuf);
    if (pcap == NULL) {
        free(handle);
        free(name);
        perror(errbuf);
        return NULL;
    }
    handle->interface = name;
    handle->handle = pcap;
    return handle;
}

void pch_send(pcap_handle* handle, const unsigned char* data, int length) {
    pcap_sendpacket((pcap_t*) handle->handle, data, length);
}

void packet_handler(u_char* user_data, const struct pcap_pkthdr* header, const u_char* packet) {
    pcap_handle* handle = (pcap_handle*) user_data;
    int length = header->caplen;
    raw_handler(handle, (const unsigned char*) packet, length);
}

void pch_listen(pcap_handle* handle, void (*handler)(pcap_handle*, const unsigned char* data, int length)) {
    if (raw_handler == NULL) {
        raw_handler = handler;
        pcap_loop((pcap_t*) handle->handle, -1, packet_handler, (u_char*) handle);
    }
}

void pch_ignore(pcap_handle* handle) {
    pcap_breakloop((pcap_t*) handle->handle);
}

void pch_close(pcap_handle* handle) {
    pcap_close((pcap_t*) handle->handle);
    free(handle->interface);
    free(handle);
}

#endif