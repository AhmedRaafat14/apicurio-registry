package io.apicurio.registry.rules.compatibility;

import io.apicurio.registry.content.TypedContent;
import io.apicurio.registry.rules.compatibility.jsonschema.JsonSchemaDiffLibrary;
import io.apicurio.registry.rules.compatibility.jsonschema.diff.DiffType;
import io.apicurio.registry.rules.compatibility.jsonschema.diff.Difference;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class JsonSchemaCompatibilityChecker extends AbstractCompatibilityChecker<Difference> {

    @Override
    protected Set<Difference> isBackwardsCompatibleWith(String existing, String proposed,
            Map<String, TypedContent> resolvedReferences) {
        return JsonSchemaDiffLibrary.getIncompatibleDifferences(existing, proposed, resolvedReferences);
    }

    @Override
    protected CompatibilityDifference transform(Difference original) {
        return new JsonSchemaCompatibilityDifference(original);
    }

    private boolean isOptionalPropertyDiff(CompatibilityDifference diff) {
        return diff.asRuleViolation().getDescription()
                .equalsIgnoreCase(DiffType.OBJECT_TYPE_PROPERTY_SCHEMAS_EXTENDED.getDescription())
                || diff.asRuleViolation().getDescription()
                        .equalsIgnoreCase(DiffType.OBJECT_TYPE_PROPERTY_SCHEMAS_NARROWED.getDescription());
    }

    @Override
    protected CompatibilityExecutionResult handleDifferencesBasedOnLevel(Set<CompatibilityDifference> diffs,
            CompatibilityLevel level) {
        if (level == CompatibilityLevel.FULL) {
            Set<CompatibilityDifference> filteredDiffs = diffs.stream()
                    .filter(diff -> !isOptionalPropertyDiff(diff)).collect(Collectors.toSet());
            return CompatibilityExecutionResult.incompatibleOrEmpty(filteredDiffs);
        }
        return CompatibilityExecutionResult.incompatibleOrEmpty(diffs);
    }

}
