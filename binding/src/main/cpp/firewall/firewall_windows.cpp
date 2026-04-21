#if defined(_WIN32) || defined(_WIN64)
#include "firewall.h"

firewall_t* fw_start(int protocol, int port, int action) {
    return NULL;
}

int fw_write_rule(firewall_t* firewall, int action, uint32_t network, uint32_t broadcast) {
    return 0;
}

int fw_commit(firewall_t* firewall) {
    return 0;
}

void fw_stop(firewall_t* firewall) {
    
}

#endif