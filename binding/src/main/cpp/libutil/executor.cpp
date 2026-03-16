#include "executor.h"

#include <stdio.h>
#include <stdlib.h>
#include <string.h>

int exec_streamable(const char* cmd, void (*consumer)(const char*)) {
    FILE* fp = popen(cmd, "r");
    if (fp == NULL) {
        return -1;
    }
    char buf[1024];
    while (fgets(buf, sizeof(buf), fp) != NULL) {
        consumer(buf);
    }
    pclose(fp);
    return 0;
}