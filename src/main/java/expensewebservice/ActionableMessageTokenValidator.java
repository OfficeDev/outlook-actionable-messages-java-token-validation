//------------------------------------------------------------------------------
//
// Copyright (c) Microsoft Corporation.
// All rights reserved.
//
// This code is licensed under the MIT License.
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files(the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and / or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions :
//
// The above copyright notice and this permission notice shall be included in
// all copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
// THE SOFTWARE.
//
//------------------------------------------------------------------------------

package expensewebservice;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.*;
import java.net.*;
import java.net.URL;
import java.text.*;
import java.util.List;
import java.util.Objects;

import com.nimbusds.jose.*;
import com.nimbusds.jose.jwk.source.*;
import com.nimbusds.jose.proc.*;
import com.nimbusds.jwt.*;
import com.nimbusds.jwt.proc.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
* Token validator for actionable message.
*/
public class ActionableMessageTokenValidator {
    private static final String O365_APP_ID = "48af08dc-f6d2-435f-b2a7-069abd99c086";
    private static final String O365_OPENID_METADATA_URL = "https://substrate.office.com/sts/common/.well-known/openid-configuration";
    private static final String O365_TOKEN_ISSUER = "https://substrate.office.com/sts/";

    /**
    * Constructor.
    */
    public ActionableMessageTokenValidator() {
    }
    
    /**
    * Validates an actionable message token.
    * @param token The token issued by Microsoft.
    * @param targetUrl The target service base URL expected in the token. For example if the target service URL is https://api.xyz.com/expense/approve, the target service base URL is https://api.xyz.com.
    * @return ActionableMessageTokenValidationResult The result of the validation.
    */
    public ActionableMessageTokenValidationResult validateToken(String token, String targetUrl) throws IOException, ParseException, MalformedURLException, BadJOSEException, JOSEException {
        OpenIdMetadata config = new OpenIdMetadata(O365_OPENID_METADATA_URL);
        JWT jwt = JWTParser.parse(token);
        JWSAlgorithm expectedJWSAlg = JWSAlgorithm.parse(jwt.getHeader().getAlgorithm().getName());
    
        JWKSource keySource = new RemoteJWKSet(new URL(config.getJsonWebKeyUrl()));
        JWSKeySelector keySelector = new JWSVerificationKeySelector(expectedJWSAlg, keySource);
        
        ConfigurableJWTProcessor jwtProcessor = new DefaultJWTProcessor();
        jwtProcessor.setJWSKeySelector(keySelector);
        
        JWTClaimsSet claimsSet = jwtProcessor.process(token, null);
        return verifyClaims(claimsSet, targetUrl);
    }

    /**
    * Validates a set of claims in a JWT token.
    * @param claims The claims set to validate.
    * @param targetUrl The expected URL in the audience claim.
    * @return ActionableMessageTokenValidationResult The result of the validation.
    */
    private ActionableMessageTokenValidationResult verifyClaims(JWTClaimsSet claims, String targetUrl) {
        ActionableMessageTokenValidationResult result = new ActionableMessageTokenValidationResult();
        
        try {
            if (!O365_TOKEN_ISSUER.equalsIgnoreCase(claims.getIssuer())) {
                result.setError(new IllegalStateException("Invalid token issuer."));
                return result;
            }
            
            List<String> audiences = claims.getAudience();
            if (audiences.size() != 1) {
                result.setError(new IllegalStateException("Audience not found in the token."));
                return result;
            }
            
            if (!targetUrl.equalsIgnoreCase(audiences.get(0))) {
                result.setError(new IllegalStateException("Invalid token audience."));
                return result;
            }
            
            if (!O365_APP_ID.equalsIgnoreCase(claims.getStringClaim("appid"))) {
                result.setError(new IllegalStateException("Invalid token appid."));
                return result;
            }
            
            result.setValidationSucceeded(true);
            result.setSender(claims.getStringClaim("sender"));
            result.setActionPerformer(claims.getSubject());
        }
        catch (Exception ex) {
            result.setError(ex);
        }
        
        return result;
    }
}