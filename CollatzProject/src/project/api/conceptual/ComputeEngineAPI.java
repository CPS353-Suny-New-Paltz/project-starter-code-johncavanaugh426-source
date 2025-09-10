package project.api.conceptual;

import project.annotations.ConceptualAPI;

@ConceptualAPI
public interface ComputeEngineAPI {
    ComputeResult computeCollatz(ComputeRequest request);
}
