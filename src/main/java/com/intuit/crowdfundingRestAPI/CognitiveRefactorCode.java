@RestController
@RequestMapping("/api")
public class RelationshipVisualizationController {

    private final RelationshipVisualizationService relationshipVisualizationService;

    public RelationshipVisualizationController(RelationshipVisualizationService relationshipVisualizationService) {
        this.relationshipVisualizationService = relationshipVisualizationService;
    }

    @GetMapping("/relationship-visualization/{id}")
    public ResponseEntity<RelationshipVisualization> getRelationshipVisualization(
            @PathVariable("id") String id, 
            HttpServletRequest request) throws ServiceException {

        try {
            LOG.info("getRelationshipVisualization called with ID={}", id);
            AuthUtils.getAuthUser(request);

            RelationshipVisualization relationshipVisualization = relationshipVisualizationService.getRelationshipVisualizationById(id);

            return ResponseEntity.ok(relationshipVisualization);
        } catch (Exception ex) {
            LOG.error("Error fetching relationship visualization for ID={}", id, ex);
            throw new ServiceException("Failed to get relationship visualization", ex);
        }
    }
}
