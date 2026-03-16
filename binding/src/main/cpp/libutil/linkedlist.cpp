#include "linkedlist.h"

#include <stdlib.h>
#include <string.h>

linked_list_t* ll_new() {
    linked_list_t* list = (linked_list_t*) malloc(sizeof(linked_list_t));
    if (list == NULL) {
        return NULL;
    }
    list->head = NULL;
    list->tail = NULL;
    list->size = 0;
    return list;
}

int ll_add(linked_list_t* list, void* value) {
    list->size++;
    ll_node_t* node = (ll_node_t*) malloc(sizeof(ll_node_t));
    if (node == NULL) {
        return 1;
    }
    node->value = value;
    node->next = NULL;
    node->prev = list->tail;
    if (list->head == NULL) {
        list->head = node;
        list->tail = node;
    } else {
        list->tail->next = node;
        list->tail = node;
    }
    return 0;
}

int ll_add_string(linked_list_t* list, const char* str) {
    char* copy = strdup(str);
    if (copy == NULL) {
        return 1;
    }
    return ll_add(list, copy);
}

void ll_destroy(linked_list_t* list, void (*function)(void*)) {
    ll_node_t* node = list->head;
    while (node != NULL) {
        ll_node_t* next = node->next;
        function(node->value);
        free(node);
        node = next;
    }
    free(list);
}