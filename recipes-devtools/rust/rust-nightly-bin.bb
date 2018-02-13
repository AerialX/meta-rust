DESCRIPTION = "rust"
HOMEPAGE = "http://rust-lang.org"
LICENSE = "CLOSED"

include rust-nightly.inc

SRC_URI += " \
	http://static.rust-lang.org/dist/${RUST_NIGHTLY_DATE}/rust-nightly-x86_64-unknown-linux-gnu.tar.gz?date=${RUST_NIGHTLY_DATE}.tar.gz \
"
SRC_URI[md5sum] := "${RUST_NIGHTLY_MD5}"
SRC_URI[sha256sum] := "${RUST_NIGHTLY_SHA256}"

S = "${WORKDIR}"
FILES_${PN} = "${bindir} ${libdir} ${sysconfdir}"
PV = "${RUSTC_VERSION}-${RUST_NIGHTLY_DATE}"
PROVIDES += "rustc cargo"

do_compile() {
	true
}

do_install() {
	${S}/rust-nightly-x86_64-unknown-linux-gnu/install.sh \
		--without=rust-docs \
		--destdir="${D}" \
		--prefix="${prefix}"
}

BBCLASSEXTEND = "native nativesdk"
