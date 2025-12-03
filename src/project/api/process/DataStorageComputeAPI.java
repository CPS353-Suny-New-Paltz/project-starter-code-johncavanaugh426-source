package project.api.process;

import project.annotations.ProcessAPI;
import java.util.List;

@ProcessAPI
public interface DataStorageComputeAPI {
    ProcessResult processData(ProcessRequest request);

    // New method: read input file
    List<String> readInputFile(String inputPath) throws Exception;
}
