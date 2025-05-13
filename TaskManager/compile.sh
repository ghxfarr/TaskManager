#!/bin/bash
javac -cp "lib/gson-2.10.1.jar" -d out src/controller/*.java src/model/*.java src/view/*.java
echo "✅ Compilation complete."