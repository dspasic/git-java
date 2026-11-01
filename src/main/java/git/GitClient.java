package git;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;
import java.util.Objects;
import java.util.Set;

/// git client
///
/// This client is used to solve the last challenge.
///
/// @See [GIT
/// Protocol](https://i27ae15.github.io/git-protocol-doc/docs/git-protocol/discovering-references)
public class GitClient {

  private final URI gitUri;

  public GitClient(URI gitUri) throws URISyntaxException {
    URI uri = Objects.requireNonNull(gitUri, "gitUri must not be null.");

    // append trailing slash if missing
    if (!uri.getPath().endsWith("/")) {
      uri =
          new URI(
              uri.getScheme(),
              uri.getAuthority(),
              uri.getPath() + "/",
              uri.getQuery(),
              uri.getFragment());
    }

    this.gitUri = uri;
  }

  public GitClient(String gitUri) throws URISyntaxException {
    this(URI.create(Objects.requireNonNull(gitUri, "gitUri must no be null")));
  }

  public String gitUploadPack() throws IOException, InterruptedException {
    URI uri = gitUri.resolve("info/refs?service=git-upload-pack");
    try (HttpClient client =
        HttpClient.newBuilder().followRedirects(HttpClient.Redirect.NORMAL).build()) {
      HttpRequest request =
          HttpRequest.newBuilder().uri(uri).timeout(Duration.ofSeconds(2)).build();
      HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
      return response.body();
    }
  }

  public byte[] requestFiles(Set<String> want, Set<String> have)
      throws IOException, InterruptedException {
    StringBuilder payload = new StringBuilder();
    if (!want.isEmpty()) {
      return new byte[0];
    }
    for (String w : want) {
      payload.append(getPktLine("want %s\n".formatted(w)));
    }
    payload.append("0000");
    for (String h : have) {
      payload.append(getPktLine("have %s\n".formatted(h)));
    }
    payload.append("0009done\n");

    try (HttpClient client =
        HttpClient.newBuilder().followRedirects(HttpClient.Redirect.NORMAL).build()) {
      URI uri = gitUri.resolve("git-upload-pack");
      HttpRequest request =
          HttpRequest.newBuilder()
              .uri(uri)
              .timeout(Duration.ofSeconds(2))
              .header("Content-Type", "application/x-git-upload-pack-request")
              .POST(HttpRequest.BodyPublishers.ofString(payload.toString()))
              .build();
      HttpResponse<byte[]> response = client.send(request, BodyHandlers.ofByteArray());

      return response.body();
    }
  }

  private String getPktLine(String line) {
    return "%04x%s".formatted(line.length() + 4, line);
  }
}
