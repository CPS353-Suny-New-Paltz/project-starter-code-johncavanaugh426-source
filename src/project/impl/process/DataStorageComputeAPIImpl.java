package project.impl.process;
import project.api.process.DataStorageComputeAPI;
import project.api.process.ProcessRequest;
import project.api.process.ProcessResult;
import project.api.conceptual.ComputeEngineAPI;

public class DataStorageComputeAPIImpl implements DataStorageComputeAPI {
    private final ComputeEngineAPI computeEngine;
    
    public DataStorageComputeAPIImpl() {
        this(null);
    }
    public DataStorageComputeAPIImpl(ComputeEngineAPI computeEngine) {
        this.computeEngine = computeEngine;
    }

    @Override
    public ProcessResult processData(ProcessRequest request) {
        return new ProcessResult(false, "Not implemented yet ");
    }
}
