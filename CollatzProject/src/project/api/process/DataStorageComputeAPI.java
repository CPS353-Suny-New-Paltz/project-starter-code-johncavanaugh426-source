package project.api.process;

import project.annotations.ProcessAPI;

@ProcessAPI
public interface DataStorageComputeAPI {
    ProcessResult processData(ProcessRequest request);
}
