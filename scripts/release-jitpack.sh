#!/usr/bin/env bash

set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$ROOT_DIR"

readonly REPO_SLUG="qinweiforandroid/SmartRefreshRecyclerView"
readonly JITPACK_BUILD_URL="https://jitpack.io/#${REPO_SLUG}"
readonly GROUP_ID="com.github.qinweiforandroid.SmartRefreshRecyclerView"
readonly MODULES=(
  "recyclerview-core"
  "recyclerview-swiperefresh"
  "recyclerview-smartrefreshlayout"
)

usage() {
  cat <<'EOF'
Usage:
  ./scripts/release-jitpack.sh <tag> [options]

Examples:
  ./scripts/release-jitpack.sh 4.2.0701
  ./scripts/release-jitpack.sh 4.2.0701 --skip-checks

Options:
  --skip-checks   Skip Gradle release build verification.
  -h, --help      Show this help message.
EOF
}

fail() {
  echo "Error: $*" >&2
  exit 1
}

require_cmd() {
  command -v "$1" >/dev/null 2>&1 || fail "Missing required command: $1"
}

TAG=""
SKIP_CHECKS=false

while (($# > 0)); do
  case "$1" in
    --skip-checks)
      SKIP_CHECKS=true
      ;;
    -h|--help)
      usage
      exit 0
      ;;
    -*)
      fail "Unknown option: $1"
      ;;
    *)
      if [[ -n "$TAG" ]]; then
        fail "Only one tag can be provided"
      fi
      TAG="$1"
      ;;
  esac
  shift
done

[[ -n "$TAG" ]] || {
  usage
  exit 1
}

require_cmd git
require_cmd ./gradlew

git rev-parse --is-inside-work-tree >/dev/null 2>&1 || fail "Current directory is not a Git repository"

REMOTE_URL="$(git remote get-url origin 2>/dev/null || true)"
[[ -n "$REMOTE_URL" ]] || fail "Git remote 'origin' is not configured"

if git rev-parse --verify --quiet "refs/tags/${TAG}" >/dev/null; then
  fail "Tag ${TAG} already exists locally"
fi

if git ls-remote --exit-code --tags origin "refs/tags/${TAG}" >/dev/null 2>&1; then
  fail "Tag ${TAG} already exists on origin"
fi

if [[ -n "$(git status --porcelain)" ]]; then
  fail "Working tree is not clean. Commit or stash your changes before releasing."
fi

CURRENT_BRANCH="$(git rev-parse --abbrev-ref HEAD)"
CURRENT_COMMIT="$(git rev-parse --short HEAD)"

echo "Preparing JitPack release"
echo "Repository : ${REPO_SLUG}"
echo "Branch     : ${CURRENT_BRANCH}"
echo "Commit     : ${CURRENT_COMMIT}"
echo "Tag        : ${TAG}"

if [[ "$SKIP_CHECKS" == false ]]; then
  echo
  echo "Running release verification builds..."
  ./gradlew \
    :recyclerview_core:assembleRelease \
    :recyclerview_swiperefresh:assembleRelease \
    :recyclerview_smartrefreshlayout:assembleRelease
fi

echo
echo "Creating Git tag ${TAG}..."
git tag "${TAG}"

echo "Pushing branch ${CURRENT_BRANCH}..."
git push origin "${CURRENT_BRANCH}"

echo "Pushing tag ${TAG}..."
git push origin "${TAG}"

echo
echo "Release submitted to JitPack."
echo "Build page:"
echo "  ${JITPACK_BUILD_URL}"
echo
echo "Dependency coordinates:"
for module in "${MODULES[@]}"; do
  echo "  implementation \"${GROUP_ID}:${module}:${TAG}\""
done
echo
echo "Tip:"
echo "  Open ${JITPACK_BUILD_URL} and select tag ${TAG} to verify the build status."
