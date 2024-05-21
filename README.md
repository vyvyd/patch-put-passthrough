# patch-put-passthrough

A short spike to see how to pass-through put and patch requests

## Premise 
![](http://www.plantuml.com/plantuml/png/VSyn2y8m40NWFR_YqKb1wUuWBGxMBM2SYiD9ZhPWLPBJKiJ_RjEcZcx9vNjvNEQ5d3pRgreiKHYZGHwro4ELUnBkg4Uk3-SmhQQxAWsvYWN7tWktlsSPfygIq8KyOpwecCDw6mqsI7HkTiKA-cIGtfrbbxuR-qbKg0uoXlxH-Dc0vwe1UtenHeMkaHeJpVXcXns_?raw=true)
1. We have two services, each with API endpoints.

2. One of these services is a 'gateway' that forwards requests to the other 'target' service.

3. The 'target' service has a PUT endpoint for replacing an entire resource and a PATCH endpoint for partially updating a resource. 

4. The PATCH endpoint on the target service must be able to reset fields to 'null'.

## Technical Challenge  

How should we model our DTOs for the two API endpoints in this pass-through setup?

## Tooling 

1. Kotlin SpringBoot 3 application setup
2. Open Feign + OkHTTP support (as the default client does not support PATCH)
3. Jackson for serialization and de-serialization (default SpringBoot 3 configuration)

## Solution 

With the use of of Java 8 Optional and with the out-of-the-box SpringBoot 3 ObjectMapper configuration, we can model a DTO

**In the 'Target' API**

1. For PUT operations 

```kotlin
data class OrderEntity(
    val consignee:String,
    val deliveryArea: String,
    val comment: String?
)
```

2. and for PATCH operations 

```kotlin
@JsonInclude(JsonInclude.Include.NON_EMPTY)
data class OrderPatchDTO(
    val consignee: Optional<String> = Optional.empty(),
    val deliveryArea: Optional<String> = Optional.empty(),
    val comment: Optional<String?>? = null
)
```
The `Optional` of a nullable-string, with a default null value (as seen on the `comment` field) - allows us to have a field in a DTO that is nullable, and can be patched to be reset to a null value. 

**In the 'Gateway' API**

For both PUT and PATCH requests

```kotlin
@JsonInclude(JsonInclude.Include.NON_EMPTY)
data class PassThroughOrderDTO(
    val id: Optional<Int?> ? = null,
    val consignee: Optional<String?>? = null,
    val deliveryArea: Optional<String?>? = null,
    val comment: Optional<String?>? = null
)
```

This DTO resembles the PATCH DTO in the target system because the gateway service forwards the request to the target system as-is, and hence we choose the lowest-common-denominator to serve as a so-called shared DTO.

## How does the PATCH work? 

|Condition   |  Mapping to Optional | Explanation  |
|---|---|---|
| The field has a value  | Optional.of(value)  |The field is updated with the provided value  |
|  The field is explicitly set to null | Optional.of(null)  |  The field is explicitly updated to null |
|  The field is omitted | null  |  The field is left unchanged; no update is made. |

### Explanation

The Optional<T?>? type in our DTOs maps these conditions as follows:

- Having a value: The field is wrapped in Optional.of(value)  


- Explicitly set to null: The field is wrapped in Optional.of(null)  


- Omitted field: The field is set to null  

### Usage

At the service layer, we utilize this tri-state value (Optional<T?>?) to selectively update the corresponding entity in the database based on whether the field has a value, is set to null, or is omitted.

Ensure to include the `@JsonInclude(JsonInclude.Include.NON_EMPTY)` annotation in DTOs that accept missing fields. Without this, Jackson will convert missing fields to `Optional.empty()`.

## Final Thoughts 

Ideally, we would avoid using Java API constructs in a Kotlin project. We could create a custom sealed class to mimic this behavior, but that would require custom serializers and deserializers.