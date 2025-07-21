package edu.appstate.cs.checker;

import com.google.auto.service.AutoService;
import com.google.errorprone.BugPattern;
import com.google.errorprone.VisitorState;
import com.google.errorprone.bugpatterns.BugChecker;
import com.google.errorprone.matchers.Description;
import com.sun.source.tree.*;

import javax.lang.model.element.Name;

import static com.google.errorprone.BugPattern.LinkType.CUSTOM;
import static com.google.errorprone.BugPattern.SeverityLevel.WARNING;

@AutoService(BugChecker.class)
@BugPattern(
        name = "BadNamesChecker",
        summary = "Poor-quality identifiers",
        severity = WARNING,
        linkType = CUSTOM,
        link = "https://github.com/plse-Lab/"
)
public class BadNamesChecker extends BugChecker implements
        BugChecker.IdentifierTreeMatcher,
        BugChecker.MethodInvocationTreeMatcher,
        BugChecker.MethodTreeMatcher {

    @java.lang.Override
    public Description matchIdentifier(IdentifierTree identifierTree, VisitorState visitorState) {
        // NOTE: This matches identifier uses. Do we want to match these,
        // or just declarations?
        Name identifier = identifierTree.getName();
        return checkName(identifierTree, identifier);
    }

    @Override
    public Description matchMethodInvocation(MethodInvocationTree methodInvocationTree, VisitorState visitorState) {
        // NOTE: Similarly to the above, this matches method names in method
        // calls. Do we want to match these, or just declarations?
        Tree methodSelect = methodInvocationTree.getMethodSelect();

        Name identifier;

        if (methodSelect instanceof MemberSelectTree) {
            identifier = ((MemberSelectTree) methodSelect).getIdentifier();
        } else if (methodSelect instanceof IdentifierTree) {
            identifier = ((IdentifierTree) methodSelect).getName();
        } else {
            throw malformedMethodInvocationTree(methodInvocationTree);
        }

        return checkName(methodInvocationTree, identifier);
    }

    @Override
    public Description matchMethod(MethodTree methodTree, VisitorState visitorState) {
        // MethodTree represents the definition of a method. We want to check the name of this
        // method to see if it is acceptable.

        // TODO: What needs to be done here to check the name of the method?
        Name methodName = methodTree.getName();
        if (methodTree.getName() == null) {
            // If the method name is null, we can't check it.
            return Description.NO_MATCH;
        }
        // Check the method name using the same logic as for identifiers.
        Description description = checkName(methodTree, methodName);
        if (description != Description.NO_MATCH) {
            return description;
        }
       
        return Description.NO_MATCH;
    }

    private Description checkName(Tree tree, Name identifier) {
        // TODO: What other names are a problem? Add checks for them here...
        if (identifier.contentEquals("foo") || identifier.contentEquals("bar")) {
            return buildDescription(tree)
                    .setMessage(String.format("%s is a bad identifier name", identifier))
                    .build();
        }

        String badName = identifier.toString();
        if (badName.length() < 3 || badName.length() > 20) {
            // If the name is too short or too long, we consider it a bad name.
            return buildDescription(tree)
                    .setMessage(String.format("Identifier '%s' is too short or too long (must be between 3 and 20 characters)", badName))
                    .build();
        }

        return Description.NO_MATCH;
    }

    private static final IllegalStateException malformedMethodInvocationTree(MethodInvocationTree tree) {
        return new IllegalStateException(String.format("Method name %s is malformed.", tree));
    }
}