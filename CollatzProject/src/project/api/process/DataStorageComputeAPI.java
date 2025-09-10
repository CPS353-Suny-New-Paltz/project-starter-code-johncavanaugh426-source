package project.api.process;

import project.annotations.ProcessAPI;

@ProcessAPI
public interface DataStorageComputeAPI {
    Processresult processData(ProcessRequest request);
}
