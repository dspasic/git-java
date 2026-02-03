# git-java
Simple Git implemented in Java

## Features

- Basic git commands: `status`, `add`, `commit`
- Quiet mode support with `--quiet` or `-q` flag

## Building

```bash
mvn clean install
```

## Running

```bash
# Verbose mode (default)
java -cp target/classes com.github.dspasic.gitjava.GitJava status

# Quiet mode - suppresses informational output
java -cp target/classes com.github.dspasic.gitjava.GitJava --quiet status
java -cp target/classes com.github.dspasic.gitjava.GitJava -q add file.txt
```

## Testing

```bash
mvn test
```

All tests should pass (13 tests total).

