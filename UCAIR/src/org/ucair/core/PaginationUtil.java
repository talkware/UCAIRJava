package org.ucair.core;

import com.google.common.base.Preconditions;

public class PaginationUtil {

    public static final int PAGE_SIZE = 10;

    public static final int MAX_PAGE_NUM = 1000;

    public static int getPageNum(final long startPos) {
        Preconditions.checkArgument(startPos >= 0, "Invalid start pos: %s",
                startPos);
        if (startPos == 0) {
            return 0;
        }
        long pageNum = (startPos - 1) / PAGE_SIZE + 1;
        if (pageNum > MAX_PAGE_NUM) {
            pageNum = MAX_PAGE_NUM;
        }
        return (int) pageNum;
    }

    public static int getStartPos(final int pageNum) {
        Preconditions.checkArgument(pageNum >= 0 && pageNum <= MAX_PAGE_NUM,
                "Invalid page num: %s", pageNum);
        if (pageNum == 0) {
            return 0;
        }
        return (pageNum - 1) * PAGE_SIZE + 1;
    }
}
