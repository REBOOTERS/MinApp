
## How To Use

[Android代码规范利器： Checkstyle](https://zhuanlan.zhihu.com/p/25004066)

### pre-commit

```shell
#!/bin/sh
#
# An example hook script to verify what is about to be committed.
# Called by "git commit" with no arguments.  The hook should
# exit with non-zero status after issuing an appropriate message if
# it wants to stop the commit.
#
# To enable this hook, rename this file to "pre-commit".

if git rev-parse --verify HEAD >/dev/null 2>&1
then
  against=HEAD
else
  # Initial commit: diff against an empty tree object
  against=4b825dc642cb6eb9a060e54bf8d69288fbee4904
fi

global_repository_email=$(git config --global user.email)
global_repository_name=$(git config --global user.name)
repository_email=$(git config user.email)
repository_name=$(git config user.name)
real_email="yingkongshi11@gmail.com"
real_name="rookie"

echo "global-git-config ====> $global_repository_email , $global_repository_name"
echo "local--git-config ====> $repository_email , $repository_name"
if test $repository_email != $real_email
then
  echo "email = $repository_email need change!!!!"
  exit 1
fi

if test $repository_name != $real_name
then
  echo "name = $repository_name need change!!!!"
  exit 1
fi

SCRIPT_DIR=$(dirname "$0")
SCRIPT_ABS_PATH=`cd "$SCRIPT_DIR"; pwd`
$SCRIPT_ABS_PATH/../../gradlew checkstyle
if [ $? -eq 0   ]; then
    echo "checkstyle OK"
else
    exit 1
fi


# If you want to allow non-ASCII filenames set this variable to true.
allownonascii=$(git config --bool hooks.allownonascii)

# Redirect output to stderr.
exec 1>&2

# Cross platform projects tend to avoid non-ASCII filenames; prevent
# them from being added to the repository. We exploit the fact that the
# printable range starts at the space character and ends with tilde.
if [ "$allownonascii" != "true" ] &&
  # Note that the use of brackets around a tr range is ok here, (it's
  # even required, for portability to Solaris 10's /usr/bin/tr), since
  # the square bracket bytes happen to fall in the designated range.
  test $(git diff --cached --name-only --diff-filter=A -z $against |
    LC_ALL=C tr -d '[ -~]\0' | wc -c) != 0
then
  cat <<\EOF
Error: Attempt to add a non-ASCII file name.

This can cause problems if you want to work with people on other platforms.

To be portable it is advisable to rename the file.

If you know what you are doing you can disable this check using:

  git config hooks.allownonascii true
EOF
  exit 1
fi

# If there are whitespace errors, print the offending file names and fail.
exec git diff-index --check --cached $against --
```