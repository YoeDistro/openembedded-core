SRCBRANCH ?= "master"
PV = "2.34"
SRCREV_glibc ?= "ae830b2d9f5238e1bee9820cd4d4df7f7b13ecff"
SRCREV_localedef ?= "95c0221703ad970a52445e9eaf91c4aff35eebef"

GLIBC_GIT_URI ?= "git://sourceware.org/git/glibc.git"

UPSTREAM_CHECK_GITTAGREGEX = "(?P<pver>\d+\.\d+(\.(?!90)\d+)*)"
