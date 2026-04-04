package me.spencernold.janus.fw;

import me.spencernold.janus.address.IpRange;

public record Rule(String name, Action action, IpRange target) {}
