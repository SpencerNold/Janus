package me.spencernold.janus.fw;

import me.spencernold.janus.address.Ip4Range;

public record Rule(String name, Action action, Ip4Range target) {}
