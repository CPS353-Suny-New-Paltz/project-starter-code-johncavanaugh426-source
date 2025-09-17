package project.impl.network;

import project.api.network.UserComputeAPI;
import project.api.network.UserComputeRequest;
import project.api.network.UserComputeResult;
import project.api.process.DataStorageComputeAPI;
import project.api.conceptual.ComputeEngineAPI;

public class UserComputeAPIImpl implements UserComputeAPI {
    private final DataStorageComputeAPI dataStore;
    private final ComputeEngineAPI computeEngine;
    public UserComputeAPIImpl() {
        this(null, null);
    }

    public UserComputeAPIImpl(DataStorageComputeAPI dataStore, ComputeEngineAPI computeEngine) {
        this.dataStore = dataStore;
        this.computeEngine = computeEngine;
    }

    @Override
    public UserComputeResult processInput(UserComputeRequest request) {
        return new UserComputeResult(false, "Not implemented yet");
    }
}
