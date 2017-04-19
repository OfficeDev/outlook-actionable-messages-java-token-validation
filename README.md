# Action Request Token Verification Java Sample

Services can send actionable messages to users to complete simple tasks against their services. When users perform one of the actions in the messages, an action request will be sent by Microsoft to the service. The request from Microsoft will contain a bearer token in the authorization header. This code sample shows how to verify the token to ensure the action request is from Microsoft, and use the claims in the token to validate the request.

        @RequestMapping(value="/api/expense", method=RequestMethod.POST)
        public ResponseEntity<?> post(
            @RequestHeader(value="Authorization") String auth) {
            
            // Replace https://api.contoso.com with your service domain URL.
            // For example, if the service URL is https://api.xyz.com/finance/expense?id=1234,
            // then replace https://api.contoso.com with https://api.xyz.com
            ActionableMessageTokenValidationResult result = validateToken(auth, "https://api.contoso.com");
            
            if (!result.getValidationSucceeded()) {
                if (result.getError() != null) {
                    System.err.println(result.getError().toString());
                }

                return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
            } 
            
            // We have a valid token. We will verify the sender and the action performer. 
            // You should replace the code below with your own validation logic.
            // In this example, we verify that the email is sent by Contoso LOB system
            // and the action performer has to be someone with @contoso.com email.
            //
            // You should also return the CARD-ACTION-STATUS header in the response.
            // The value of the header will be displayed to the user.
            if (!Objects.equals(result.getSender().toLowerCase(), "lob@contoso.com") ||
                !result.getActionPerformer().toLowerCase().endsWith("@contoso.com")) {
                HttpHeaders headers = new HttpHeaders();
                headers.add("CARD-ACTION-STATUS", "Invalid sender or the action performer is not allowed.");
                return new ResponseEntity<>(null, headers, HttpStatus.FORBIDDEN);
            }
            
            // Further business logic code here to process the expense report.
            
            HttpHeaders headers = new HttpHeaders();
            headers.add("CARD-ACTION-STATUS", "The expense was approved.");
            return new ResponseEntity<>(null, headers, HttpStatus.OK);
        }

        private ActionableMessageTokenValidationResult validateToken(String authHeader, String targetUrl) {
            ActionableMessageTokenValidationResult result = new ActionableMessageTokenValidationResult();

            try {

                String[] tokens = authHeader.split(" ");
                if (tokens.length != 2) {
                    result.setError(new IllegalStateException("Invalid token."));
                    return result;
                }

                ActionableMessageTokenValidator validator = new ActionableMessageTokenValidator();

                // ValidateToken will verify the following
                // 1. The token is issued by Microsoft and its digital signature is valid.
                // 2. The token has not expired.
                // 3. The audience claim matches the service domain URL.
                result = validator.validateToken(tokens[1], targetUrl);
            }
            catch (Exception ex) {
                result.setError(ex);
            }

            return result;
        }

The code sample is using the following library for JWT validation.   

[OAuth 2.0 SDK With OpenID Connect Extensions 5.17.1](https://mvnrepository.com/artifact/com.nimbusds/oauth2-oidc-sdk/5.17.1)   

More information Outlook Actionable Messages is available [here](https://dev.outlook.com/actions).

## Copyright
Copyright (c) 2017 Microsoft. All rights reserved.