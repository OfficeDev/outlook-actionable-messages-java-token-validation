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
import java.lang.Iterable;
import java.net.URL;

import com.nimbusds.jose.JWSAlgorithm;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
* Represents an OpenID configuration.
*/
public class OpenIdMetadata {
    private JSONObject config;
    
    /**
    * Constructor.
    * @param url The URL to the Open ID configuration.
    */
    public OpenIdMetadata(String url) throws IOException, JSONException {
        getOpenIdConfiguration(url);
    }
    
    /**
    * Gets the URL of the JSON web key set in the configuration.
    * @return String The URL of the JSON web key set.
    */
    public String getJsonWebKeyUrl() {
        return config.get("jwks_uri").toString();
    }
    
    /**
    * Gets the Open ID configuration.
    * @param url The URL of the Open ID configuration.
    */
    private void getOpenIdConfiguration(String url) throws IOException, JSONException {
        config = getJsonFromUrl(url);
    }
    
    /**
    * Reads the JSON returned by the URL.
    * @param url The URL to read the JSON from.
    * @return JSONObject The JSON object.
    */
    private JSONObject getJsonFromUrl(String url) throws IOException, JSONException {
        BufferedReader rd = new BufferedReader(new InputStreamReader(new URL(url).openStream()));
        StringBuilder sb = new StringBuilder();
        int cp;
        
        while ((cp = rd.read()) != -1) {
            sb.append((char)cp);
        }
        
        JSONObject json = new JSONObject(sb.toString());
        
        return json;
    }
}