SRCBRANCH ?= "master"
PV = "2.43+git"
SRCREV_glibc ?= "c6908c4e241962a74aa9dc23056d332036b4879a"
SRCREV_localedef ?= "cba02c503d7c853a38ccfb83c57e343ca5ecd7e5"

GLIBC_GIT_URI ?= "git://sourceware.org/git/glibc.git;protocol=https"

UPSTREAM_CHECK_GITTAGREGEX = "(?P<pver>\d+\.\d+(\.(?!90)\d+)*)"
