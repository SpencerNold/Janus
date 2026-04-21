#include "network.h"

#ifdef _WIN32
    #include <winsock2.h>
    #include <ws2tcpip.h>
#else
    #include <arpa/inet.h>
#endif
#include <string.h>
#include <inttypes.h>

int net_get_link_layer_end(unsigned char* data, int length) {
    if (length < 14) {
        return -1;
    }
    int start = 0;
    uint16_t ethertype;
    memcpy(&ethertype, data + 12, sizeof(uint16_t));
    ethertype = ntohs(ethertype);
    if (ethertype == 0x8100) {
        return 18;
    }
    return 14;
}

int internal_ipv6_header_hop(uint8_t* data, int length, uint8_t* out_proto) {
    if (length < 40) {
        return -1;
    }
    uint8_t next = data[6];
    int offset = 40;
    while (1) {
        if (next == 6 || next == 17 || next == 58) {
            // STOP header hop
            if (out_proto != NULL) {
                *out_proto = next;
            }
            return offset;
        } else if (next == 0 || next == 43 || next == 60) {
            // Hop-by-hop, Routing, Destination Options
            if (length < (offset + 2)) {
                return -1;
            }
            uint8_t hdr_len = data[offset + 1];
            int ext_len = (hdr_len + 1) * 8;
            if (length < (offset + ext_len)) {
                return -1;
            }
            next = data[offset];
            offset += ext_len;
        } else if (next == 44) {
            // Fragment header
            if (length < (offset + 8)) {
                return -1;
            }
            next = data[offset];
            offset += 8;
        } else if (next == 50 || next == 51) {
            // ESP or AH
            return offset;
        } else {
            // Something else (unsupported)
            return -1;
        }
    }
}

int net_get_network_layer_end(unsigned char* data, int length) {
    int start = net_get_link_layer_end(data, length);
    if (start < 0 || length < (start + 1)) {
        return -1;
    }
    uint8_t* ip = data + start;
    uint8_t v_ihl = ip[0];
    uint8_t ihl = v_ihl & 0x0F;
    uint8_t v = v_ihl >> 4;
    if (v == 6) {
        // IPv6
        int len = internal_ipv6_header_hop(ip, length - start, NULL);
        if (len == -1) {
            return -1;
        }
        return start + len;
    } else if (v == 4) {
        // IPv4
        return start + (ihl * 4);
    }
    // Something else (unsupported)
    return -1;
}

int net_get_transport_layer_end(unsigned char* data, int length) {
    int ipOffs = net_get_link_layer_end(data, length);
    if (ipOffs < 0 || length < (ipOffs + 1)) {
        return -1;
    }
    uint8_t* ip = data + ipOffs;
    uint8_t v_ihl = *ip;
    uint8_t ihl = v_ihl & 0x0F;
    uint8_t v = v_ihl >> 4;
    int start = -1;
    uint8_t protocol = -1;
    if (v == 6) {
        // IPv6
        int len = internal_ipv6_header_hop(ip, length - ipOffs, &protocol);
        if (len == -1) {
            return -1;
        }
        start = ipOffs + len;
    } else if (v == 4) {
        // IPv4
        start = ipOffs + (ihl * 4);
        protocol = *(ip + 9);
    }
    if (protocol == 6) {
        // TCP
        if (length < (start + 13)) {
            return -1;
        }
        uint8_t* tcp = data + start;
        uint8_t offset = (*(tcp + 12) >> 4) & 0x0F;
        return start + (offset * 4);
    } else if (protocol == 17) {
        // UDP
        if (length < (start + 8)) {
            return -1;
        }
        return start + 8;
    }
    return -1;
}