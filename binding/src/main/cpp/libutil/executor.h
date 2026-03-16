#ifndef EXECUTOR_H
#define EXECUTOR_H

int exec_streamable(const char* cmd, void (*consumer)(const char*));

#endif