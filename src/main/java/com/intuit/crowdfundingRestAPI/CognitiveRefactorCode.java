import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;

@Data
@NoArgsConstructor
public class RelationshipGraphHierarchy {
    private final Map<String, Party> partyLookup = new HashMap<>();

    public void addParty(Party party) {
        partyLookup.put(party.getPartyId(), party);
    }

    public void addRelationship(String parentPartyId, String childPartyId, Relationship relationship) {
        Party parentParty = partyLookup.get(parentPartyId);
        Party childParty = partyLookup.get(childPartyId);
        relationship.setChildParty(childParty);
        parentParty.addRelationship(relationship);
        recalculateLevelsAndParents();
    }

    public void addRelationships(String sourcePartyId, List<Relationship> relationships) {
        Party sourceParty = partyLookup.get(sourcePartyId);
        for (Relationship relationship : relationships) {
            Party destinationParty = partyLookup.get(relationship.getChildParty().getPartyId());
            relationship.setChildParty(destinationParty);
            sourceParty.addRelationship(relationship);
        }
        recalculateLevelsAndParents();
    }

    private Map<String, Integer> levelLookup = new HashMap<>();
    private Map<String, String> longestPathParentLookup = new HashMap<>();

    private List<String[]> recalculateLevelsAndParents() {
        levelLookup.clear();
        longestPathParentLookup.clear();
        Map<String, List<String>> parentTracker = new HashMap<>();

        for (String root : partyLookup.keySet()) {
            if (!levelLookup.containsKey(root)) {
                dfsCalculateLevels(root, 0, null, parentTracker);
            }
        }

        List<String[]> multiparentChildren = new ArrayList<>();
        for (Map.Entry<String, List<String>> entry : parentTracker.entrySet()) {
            if (entry.getValue().size() > 1) {
                String child = entry.getKey();
                String longestPathParent = longestPathParentLookup.get(child);
                multiparentChildren.add(new String[]{child, longestPathParent});
            }
        }
        return multiparentChildren;
    }

    private void dfsCalculateLevels(String partyId, int level, String parent, Map<String, List<String>> parentTracker) {
        if (!levelLookup.containsKey(partyId) || level > levelLookup.get(partyId)) {
            levelLookup.put(partyId, level);
            if (parent != null) {
                longestPathParentLookup.put(partyId, parent);
                parentTracker.computeIfAbsent(partyId, k -> new ArrayList<>()).add(parent);
            }
        }

        Party party = partyLookup.get(partyId);
        if (party == null) return;

        for (Relationship relationship : party.getRelatedPartyHierarchy()) {
            String destinationPartyId = relationship.getChildParty().getPartyId();
            dfsCalculateLevels(destinationPartyId, level + 1, partyId, parentTracker);
        }
    }

    public int getPartyLevel(String partyId) {
        return levelLookup.getOrDefault(partyId, -1);
    }

    public String getLongestPathParent(String partyId) {
        return longestPathParentLookup.get(partyId);
    }
}
