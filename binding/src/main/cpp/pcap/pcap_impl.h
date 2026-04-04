#ifndef PCAP_IMPL_H
#define PCAP_IMPL_H

struct pcap_handle {
    void* handle;
    char* interface;
};

pcap_handle* pch_open();
pcap_handle* pch_open(char* interface);
void pch_send(pcap_handle* handle, const unsigned char* data, int length);
void pch_listen(pcap_handle* handle, void (*function)(pcap_handle*, const unsigned char* data, int length));
void pch_ignore(pcap_handle* handle);
void pch_close(pcap_handle* handle);

#endif