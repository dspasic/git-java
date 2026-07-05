package git;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.Objects;

public class GitClient {

  private final URI gitUri;

  public GitClient(URI gitUri) throws URISyntaxException {
    var uri = gitUri;
    if (!uri.getPath().endsWith("/")) {
      var path = uri.getPath();

      uri = new URI(uri.getScheme(), uri.getAuthority(), path + "/", uri.getQuery(), uri.getFragment());
    }
    this.gitUri = Objects.requireNonNull(uri, "gitUri must not be null.");
  }

  public GitClient(String gitUri) throws URISyntaxException {
    this(URI.create(Objects.requireNonNull(gitUri, "gitUri must no be null")));
  }

  public String gitUploadPack() throws IOException, InterruptedException {
    URI uri = gitUri.resolve("info/refs?service=git-upload-pack");
    System.out.println(uri);
    HttpClient client = HttpClient.newBuilder().followRedirects(HttpClient.Redirect.NORMAL).build();
    HttpRequest request = HttpRequest.newBuilder().uri(uri).build();

    HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
    return response.body();
  }
}
