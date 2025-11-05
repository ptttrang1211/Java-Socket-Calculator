#  Calculator Client–Server (Java, Multi-threaded TCP)

A simple **client–server calculator** built with **Java TCP sockets**.  
The server supports **multi-threading**, allowing multiple clients to connect and send math requests simultaneously.  
The client includes a **Swing GUI interface** for easy user interaction.

---

## 🔹 How It Works
1. **Server** starts and listens on the default port `1234`.
2. **Client GUI** connects and sends a request like:
ADD 5 3

3. **Server** processes the request and responds:
ANSWER:8.0

4. The GUI immediately displays the result to the user.

---

## 🔁 Communication Protocol
| Client Request | Server Response                     | Description    |
|----------------|-------------------------------------|----------------|
| `ADD 5 3`      | `ANSWER:8.0`                        | Addition       |
| `SUB 10 4`     | `ANSWER:6.0`                        | Subtraction    |
| `MUL 2 5`      | `ANSWER:10.0`                       | Multiplication |
| `DIV 10 0`     | `ERROR:RUNTIME_01:Division by zero` | Error          |

---

## ✨ Features
- 🧵 **Multi-threaded server** — handles multiple clients concurrently
- ⚙️ **Auto configuration** — reads IP and port from `server_info.dat`
- 💬 **Simple text-based protocol** — easy to debug and test
- 🪟 **Swing GUI** — interactive interface for user input/output
- 🔒 **Safe socket closing** — clean shutdown when exiting

---

## 🧪 Unit Testing
| Test Class             | Description |
|------------------------|-------------|
| `CalculatorClientTest` | Uses a mock server to verify client requests and responses |
| `CalculatorServerTest` | Starts the real server and tests protocol operations (`ADD`, `DIV`, `XYZ`, invalid formats`) |

✅ Tests automatically verify:
- Correct `ANSWER:` results
- Proper handling of errors (syntax, command, division by zero)
- Connection stability

---

## 📘 Note on JUnit Setup (Non-Maven Projects)
If your project does not use Maven:

1. Download the **JUnit 5 standalone JAR**:  
   👉 [https://search.maven.org/artifact/org.junit.platform/junit-platform-console-standalone](https://search.maven.org/artifact/org.junit.platform/junit-platform-console-standalone)
2. Add it to your project’s build path (in IntelliJ or Eclipse).
3. Then, you can directly run tests from the IDE.

---

## 👩‍💻 Author
**PHAM THI THU TRANG**  
📘 *Computer Network Assignment*  
💬 Language: **Java**  
🏫 Gachon University, Korea
