DESCRIPTION = "rust"
HOMEPAGE = "http://rust-lang.org"
LICENSE = "CLOSED"

include rust-nightly.inc

BRANCH_rust ?= "master"
BRANCH_libc ?= "master"
BRANCH_compiler-builtins ?= "master"

SRC_URI += " \
	git://github.com/rust-lang/rust.git;protocol=https;name=rust;destsuffix=git;branch=${BRANCH_rust} \
	git://github.com/rust-lang-nursery/compiler-builtins.git;protocol=https;name=compiler-builtins;destsuffix=git/src/libcompiler_builtins;branch=${BRANCH_compiler-builtins} \
	git://github.com/rust-lang/compiler-rt.git;protocol=https;name=compiler-rt;destsuffix=git/src/libcompiler_builtins/compiler-rt;branch=${BRANCH_compiler-rt} \
	git://github.com/rust-lang-nursery/libc.git;protocol=https;name=libc;destsuffix=git/src/liblibc;branch=${BRANCH_libc} \
	file://std-cargo.patch \
	file://compiler-rt.patch;patchdir=src/libcompiler_builtins/compiler-rt \
	file://${TARGET_JSON}.json \
"

DEPENDS = " \
	virtual/${TARGET_PREFIX}gcc virtual/${TARGET_PREFIX}compilerlibs virtual/libc \
	rustc-native cargo-native cmake-native \
"

S = "${WORKDIR}/git"
FILES_${PN}-dev = "${libdir}"
PV = "${SRCREV_rust}"

do_compile() {
	cd "${S}/sysroot"
	chmod +x rustc-sysroot
	export RUSTC="$PWD/rustc-sysroot"
	export LD_LIBRARY_PATH="$LD_LIBRARY_PATH:${STAGING_LIBDIR_NATIVE}"
	export TARGET_CFLAGS="${TARGET_CFLAGS} ${TARGET_CC_ARCH} --sysroot=${STAGING_DIR_TARGET}"
	export TARGET_CC="${TARGET_PREFIX}gcc"
	export CFLAGS="$TARGET_CFLAGS"
	export CC="$CC"
	export CARGO_TARGET_DIR="${B}/target"
	export RUST_TARGET_PATH="${WORKDIR}"
	cargo build --target "${TARGET_JSON}" --release -v
}

do_install() {
	DEST="${D}${libdir}/rustlib/${TARGET_JSON}/lib"
	install -d "${DEST}"

	echo "${TARGET_JSON}" > "${DEST}/../../${MACHINE}"
	install -m 0644 "${WORKDIR}/${TARGET_JSON}.json" "${DEST}/../../"
	cp "${B}/target/${TARGET_JSON}/release/deps/"* "${DEST}/"
}
