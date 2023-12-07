package com.github.le_yams.openfga4intellij.servers.service;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ServersState {
    private List<ServerState> servers = new ArrayList<>();
}
