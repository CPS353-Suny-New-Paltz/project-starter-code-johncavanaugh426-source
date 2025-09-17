package project.api.network;
import project.annotations.NetworkAPI;
@NetworkAPI
public interface UserComputeAPI {
    UserComputeResult processInput(UserComputeRequest request);
}
