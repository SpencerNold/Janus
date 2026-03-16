package me.spencernold.janus.fw;

public record Rule(String name, Action action, IpRange target) {}
