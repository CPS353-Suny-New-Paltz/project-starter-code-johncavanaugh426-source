package project.impl.conceptual;

import project.api.conceptual.ComputeEngineAPI;
import project.api.conceptual.ComputeRequest;
import project.api.conceptual.ComputeResult;

public class ComputeEngineAPIImpl implements ComputeEngineAPI {

    public ComputeEngineAPIImpl() {
     
    }

    @Override
    public ComputeResult computeCollatz(ComputeRequest request) {
        return new ComputeResult(false, "Not Implemented yet");
    }
}
