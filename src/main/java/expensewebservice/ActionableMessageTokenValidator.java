// 
// Copyright (c) Microsoft. All rights reserved.
// Licensed under the MIT license.
// 
// Copyright (c) Microsoft Corporation
// All rights reserved.
// 
// MIT License:
// Permission is hereby granted, free of charge, to any person obtaining
// a copy of this software and associated documentation files (the
// "Software"), to deal in the Software without restriction, including
// without limitation the rights to use, copy, modify, merge, publish,
// distribute, sublicense, and/or sell copies of the Software, and to
// permit persons to whom the Software is furnished to do so, subject to
// the following conditions:
// 
// The above copyright notice and this permission notice shall be
// included in all copies or substantial portions of the Software.
// 
// THE SOFTWARE IS PROVIDED ""AS IS"", WITHOUT WARRANTY OF ANY KIND,
// EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
// MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
// NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
// LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
// OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
// WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
//

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

public class ActionableMessageTokenValidator {
    public ActionableMessageTokenValidator() {
    }
    
    public ActionableMessageTokenValidationResult validateToken(String token, String targetUrl) throws IOException, JSONException, ParseException, MalformedURLException, BadJOSEException, JOSEException {
        ConfigurableJWTProcessor jwtProcessor = new DefaultJWTProcessor();
        OpenIdMetadata config = new OpenIdMetadata("https://substrate.office.com/sts/common/.well-known/openid-configuration");
        JWT jwt = JWTParser.parse(token);
        JWSAlgorithm expectedJWSAlg = JWSAlgorithm.parse(jwt.getHeader().getAlgorithm().getName());
    
        JWKSource keySource = new RemoteJWKSet(new URL(config.getJsonWebKeyUrl()));
        JWSKeySelector keySelector = new JWSVerificationKeySelector(expectedJWSAlg, keySource);
        jwtProcessor.setJWSKeySelector(keySelector);
        
        SecurityContext ctx = null;
        JWTClaimsSet claimsSet = jwtProcessor.process(token, ctx);
        ActionableMessageTokenValidationResult result = verifyClaims(claimsSet, targetUrl);
        
        return result;
    }

    private ActionableMessageTokenValidationResult verifyClaims(JWTClaimsSet claims, String targetUrl) {
        ActionableMessageTokenValidationResult result = new ActionableMessageTokenValidationResult();
        
        try {
            if (!Objects.equals(claims.getIssuer().toLowerCase(), "https://substrate.office.com/sts/")) {
                result.setError(new IllegalStateException("Invalid token issuer."));
                return result;
            }
            
            List<String> audiences = claims.getAudience();
            if (audiences.size() != 1) {
                result.setError(new IllegalStateException("Audience not found in the token."));
                return result;
            }
            
            if (!Objects.equals(audiences.get(0).toLowerCase(), targetUrl.toLowerCase())) {
                result.setError(new IllegalStateException("Invalid token audience."));
                return result;
            }
            
            if (!Objects.equals(claims.getStringClaim("appid").toLowerCase(), "48af08dc-f6d2-435f-b2a7-069abd99c086")) {
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