package InitialScheme.Sources.OntologySources.Axioms;

import InitialScheme.Mapping.RelationType;

public class AxiomType {

    public static final String Type = " Type ";
    public static final String SubClassOf = " SubClassOf ";
    public static final String SuperClassOf = " SuperClassOf ";
    public static final String PartOf = " PartOf ";
    public static final String HasPart = " HasPart ";
    public static final String IndirectSubClassOf = " IndirectSubClassOf ";
    public static final String HasLabel = " HasLabel ";
    public static final String InstanceOf = " InstanceOf ";
    public static final String Matches = " Matches ";
    public static final String Contains = " containsInstance ";
    
    public static String getAxiomType(int relationtype){
        switch(relationtype){
            case RelationType.HYPERNYM:
                return SuperClassOf;
            case RelationType.HYPONYM:
                return SuperClassOf;
            case RelationType.INSTANCE_HYPERNYM:
                return Contains;
            case RelationType.INSTANCE_HYPONYM:
                return InstanceOf;
            case RelationType.MEMBER_HOLONYM:
                return HasPart;
                case RelationType.MEMBER_MERONYM:
                return PartOf;
            case RelationType.SUBSTANCE_HOLONYM:
                return HasPart;
                case RelationType.SUBSTANCE_MERONYM:
                return PartOf;
            case RelationType.PART_HOLONYM:
                return HasPart;
                case RelationType.PART_MERONYM:
                return PartOf;
            default:
                return Matches;
        }
    }
    
}
