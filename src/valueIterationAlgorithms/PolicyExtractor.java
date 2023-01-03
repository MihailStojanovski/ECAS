package valueIterationAlgorithms;

import java.util.List;
import java.util.Map;

public interface PolicyExtractor {
    
    public Map<String, List<String>> extract(Map<String, List<String>> extractionTarget, Map<String, Map<String, Double>> qValue);
}
