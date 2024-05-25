import re


def replace_implementation(file_path):
    with open(file_path, 'r') as file:
        content = file.read()

    pattern = r'implementation\s+"([^"]*)"'
    new_content = re.sub(pattern, r'implementation("\1")', content)

    pattern = r"implementation\s+'(.*?)'"
    new_content = re.sub(pattern, r'implementation("\1")', new_content)

    pattern = r'api\s+"([^"]*)"'
    new_content = re.sub(pattern, r'api("\1")', new_content)

    pattern = r"api\s+'(.*?)'"
    new_content = re.sub(pattern, r'api("\1")', new_content)

    print(new_content)


if __name__ == '__main__':
    target_file ="../custom-gradle/coroutines-dep.gradle"
    replace_implementation(target_file)
