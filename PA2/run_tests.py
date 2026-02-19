import os
import subprocess
import re

# Configuration
TEST_DIR = "./testcases"
SOOT_JAR = "soot-4.6.0-jar-with-dependencies.jar"
REPORT_FILE = "test_report.txt"

def get_core_content(raw_stdout):
    """
    Skips the first 6 lines of the output and returns everything else.
    """
    lines = raw_stdout.splitlines()
    
    # We take from index 6 (the 7th line) to the very end.
    if len(lines) > 6:
        return "\n".join(lines[6:]).strip()
    
    return ""

def flexible_compare(str1, str2):
    """Compares strings ignoring all internal whitespace."""
    n1 = re.sub(r'\s+', '', str1)
    n2 = re.sub(r'\s+', '', str2)
    return n1 == n2

def run_tests():
    # Find folders like Test1, Test2, etc.
    folders = [f for f in os.listdir(TEST_DIR) if f.startswith("Test")]
    folders.sort(key=lambda x: int(re.findall(r'\d+', x)[0]))

    with open(REPORT_FILE, "w") as report:
        for folder in folders:
            expected_path = os.path.join(TEST_DIR, folder, folder)
            if not os.path.exists(expected_path): 
                continue

            with open(expected_path, "r") as f:
                expected = f.read().strip()

            # The fix: manually set stdout and stderr to capture everything together
            cmd = f"java -cp .:{SOOT_JAR} Main {folder}"
            process = subprocess.run(
                cmd, 
                shell=True, 
                stdout=subprocess.PIPE, 
                stderr=subprocess.STDOUT, 
                text=True
            )
            
            # Extract content starting from line index 6
            actual = get_core_content(process.stdout)

            # Compare result
            status = "PASS" if flexible_compare(expected, actual) else "FAIL"

            # Log to report
            report.write(f"{folder} : {status}\n")
            report.write(f"Expected result:\n{expected}\n\n")
            report.write(f"Actual result:\n{actual}\n")
            report.write("-" * 50 + "\n")
            
            print(f"Verified {folder}: {status}")

    print(f"\nDone! Results saved to {REPORT_FILE}")

if __name__ == "__main__":
    run_tests()