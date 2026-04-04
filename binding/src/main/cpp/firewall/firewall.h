#ifndef FIREWALL_H
#define FIREWALL_H

#ifdef __cplusplus
extern "C" {
#endif

#include <inttypes.h>

typedef struct firewall_t {
    void* handle;
    int protocol;
    int port;
    int action;
} firewall_t;

firewall_t* fw_start(int protocol, int port, int action);
int fw_write_rule(firewall_t* firewall, int action, uint32_t network, uint32_t broadcast);
int fw_commit(firewall_t* firewall);
void fw_stop(firewall_t* firewall);

#ifdef __cplusplus
}
#endif

#endif