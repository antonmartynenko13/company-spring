package com.martynenko.anton.company.report;

import java.util.List;
import java.util.Map;

public interface ReportGenerator {

  byte[] generate(Map<String, List<String[]>> content);

}
