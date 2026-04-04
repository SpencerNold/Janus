#ifndef NETWORK_H
#define NETWORK_H

int net_get_link_layer_end(unsigned char* data, int length);
int net_get_network_layer_end(unsigned char* data, int length);
int net_get_transport_layer_end(unsigned char* data, int length);

#endif