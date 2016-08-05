package org.lakunu.web.api;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import org.lakunu.labs.utils.LabUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;

public class UrlPathInfo {

    private final ImmutableList<String> pathSegments;

    UrlPathInfo(HttpServletRequest request) {
        this(request.getPathInfo());
    }

    UrlPathInfo(String pathInfo) {
        if (Strings.isNullOrEmpty(pathInfo)) {
            pathSegments = ImmutableList.of();
        } else {
            pathSegments = Arrays.stream(pathInfo.replaceAll("/+", "/").split("/"))
                    .filter(s -> !s.equals(""))
                    .collect(LabUtils.immutableList());
        }
    }

    public String get(int index) {
        return pathSegments.get(index);
    }

    public boolean isEmpty() {
        return pathSegments.isEmpty();
    }

}
