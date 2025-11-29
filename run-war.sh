#!/bin/bash

# 색상 정의
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

echo -e "${GREEN}=== WAR 파일 빌드 및 실행 ===${NC}"

# 1. 기존 8080 포트 프로세스 종료 및 데이터 정리
echo -e "\n${YELLOW}[1/4] 기존 애플리케이션 종료 및 데이터 정리 중...${NC}"
if lsof -ti:8080 > /dev/null 2>&1; then
    lsof -ti:8080 | xargs kill -9
    echo -e "${GREEN}✓ 8080 포트 프로세스 종료 완료${NC}"
else
    echo -e "${GREEN}✓ 실행 중인 프로세스 없음${NC}"
fi

# H2 데이터베이스 파일 삭제
if [ -d "data" ]; then
    rm -rf data
    echo -e "${GREEN}✓ 기존 H2 데이터베이스 삭제 완료${NC}"
fi

# 2. WAR 파일 빌드
echo -e "\n${YELLOW}[2/4] WAR 파일 빌드 중...${NC}"
./gradlew clean build -x test

if [ $? -eq 0 ]; then
    echo -e "${GREEN}✓ WAR 파일 빌드 성공${NC}"
    ls -lh build/libs/*.war
else
    echo -e "${RED}✗ WAR 파일 빌드 실패${NC}"
    exit 1
fi

# 3. WAR 파일 실행
echo -e "\n${YELLOW}[3/4] WAR 파일 실행 중...${NC}"
echo -e "${GREEN}Profile: production${NC}"
echo -e "${GREEN}Port: 8080${NC}"
echo -e "${GREEN}Database: H2 (file-based)${NC}"
echo ""

java -Dspring.profiles.active=production -jar build/libs/ROOT.war
