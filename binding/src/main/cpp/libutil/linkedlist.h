#ifndef LINKED_LIST_H
#define LINKED_LIST_H

typedef struct ll_node_t {
    void* value;
    ll_node_t* next;
    ll_node_t* prev;
} ll_node_t;

typedef struct linked_list_t {
    ll_node_t* head;
    ll_node_t* tail;
    int size;
} linked_list_t;

linked_list_t* ll_new();
int ll_add(linked_list_t* list, void* value);
int ll_add_string(linked_list_t* list, const char* str);
void ll_destroy(linked_list_t* list, void (*function)(void*));

#endif