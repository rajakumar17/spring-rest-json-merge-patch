# spring-rest-json-merge-patch
Spring REST sample with json merge patch support

There are two RFCs on the JSON patch to support 

1. JSON Merge Patch - https://tools.ietf.org/html/rfc7386
2. JavaScript Object Notation (JSON) Patch - https://tools.ietf.org/html/rfc6902

Spring REST is yet to inherently support these RFCs. Spring Data REST supports both these RFCs.

Following steps will add patch support in the REST controller
1. Consume application/merge-patch+json media type 
2. Retrieve the request body as Json String 
3. Parse the input Json String to JsonNode
4. Construct JsonMergePatch object from the above parsed JsonNode
5. Convert the existing entity object to JsonNode
6. Apply the JsonMergePatch on existing entity object
7. Convert the patched JsonNode back to entity object
8. Save the entity object to the repository
9. JSON Merge patch has limitations around merging array elements and setting null values

References

1. FGE JSON Patch library - https://github.com/fge/json-patch
2. Spring DATAREST commit - https://github.com/spring-projects/spring-data-rest/commit/ef3720be11f117bb691edbbf63e38ff72e0eb3dd
3. http://erosb.github.io/post/json-patch-vs-merge-patch/
