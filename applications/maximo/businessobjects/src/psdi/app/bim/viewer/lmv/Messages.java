/**
* Copyright IBM Corporation 2009-2017
*
* Licensed under the Eclipse Public License - v 1.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* https://www.eclipse.org/legal/epl-v10.html
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
* 
* @Author Doug Wood
**/
package psdi.app.bim.viewer.lmv;

public class Messages
{
	public final static String BUNDLE_MSG                 = "bimlmv";	
	// You must select \"Delete All Models\" and \"Delete All Viewables\" before you can delete the storage container
	public final static String MSG_BUKET_DELTE_NOT_ALLOWED = "bucket-delete-not-allowed";
	// bytes to upload
	public final static String MSG_UPLOADING_MODEL       = "uploding-model-file";
	// {0} bytes
	public final static String MSG_BYTES_TO_UPLOAD        = "bytes-to-upload";
	// A model can't be delete while an upload is in progress
	public final static String MSG_NO_DELETE_WHILE_UPLOAD = "no-delete-during-upload";
	
	// Model {0} submitted for registration.  Use the Manage Viewables dialog to check its status
	public final static String MSG_AUTO_REG               = "submit-auto-registration";
	// Upload of model complete
	public final static String MSG_UPLOAD_CONPLETE        = "upload-complete";
	
	// Bucket {0} was not found for user key {1}
	public final static String WRN_BUCKET_NOT_FOUND	      = "bucket_not_found";
	// Model {0} was not found
	public final static String WRN_MODEL_NOT_FOUND	      = "model_not_found";
	// Viewable {0} was not found
	public final static String WRN_VIEWABLE_NOT_FOUND	  = "viewable_not_found";
	
	// Error accessing the Autodesk cloud: {0} - {1}. 
	public final static String ERR_AUTODESK_API       = "autodesk_api_err";
	// Upload file checksum mismatch.
	public final static String ERR_BAD_CHECKSUM       = "bad_checksum";
	// No objects created by the upload
	public final static String ERR_NO_OBJECT          = "No_object";
	// An exception occurred while contacting the Autodesk cloud service
	public final static String ERR_NETWORK_FAULT      = "network-fault";
	
	public final static String HTTP_400 = "Bad Request";
	public final static String HTTP_401 = "Unauthorized";
	public final static String HTTP_402 = "Payment Required";
	public final static String HTTP_403 = "Forbidden";
	public final static String HTTP_404 = "Not Found";
	public final static String HTTP_405 = "Method Not Allowed";
	public final static String HTTP_406 = "Not Acceptable";
	public final static String HTTP_407 = "Proxy Authentication Required";
	public final static String HTTP_408 = "Request Timeout";
	public final static String HTTP_409 = "Conflict";
	public final static String HTTP_410 = "Gone";
	public final static String HTTP_411 = "Length Required";
	public final static String HTTP_412 = "Precondition Failed";
	public final static String HTTP_413 = "Payload Too Large";
	public final static String HTTP_414 = "URI Too Long";
	public final static String HTTP_415 = "Unsupported Media Type";
	public final static String HTTP_416 = "Range Not Satisfiable"; 
	public final static String HTTP_417 = "Expectation Failed";
	public final static String HTTP_421 = "Misdirected Request";
	public final static String HTTP_422 = "Unprocessable Entity";
	public final static String HTTP_423 = "Locked";
	public final static String HTTP_424 = "Failed Dependency"; 
	public final static String HTTP_426 = "Upgrade Required";
	public final static String HTTP_428 = "Precondition Required";
	public final static String HTTP_429 = "Too Many Requests";
	public final static String HTTP_431 = "Request Header Fields Too Large";
	public final static String HTTP_451 = "Unavailable For Legal Reasons";

	public final static String HTTP_500 = "Internal Server Error";
	public final static String HTTP_501 = "Not Implemented";
	public final static String HTTP_502 = "Bad Gateway";
	public final static String HTTP_503 = "Service Unavailable";
	public final static String HTTP_504 = "Gateway Timeout";
	public final static String HTTP_505 = "HTTP Version Not Supported";
	public final static String HTTP_506 = "Variant Also Negotiates";
	public final static String HTTP_507 = "Insufficient Storage";
	public final static String HTTP_508 = "Loop Detected";
	public final static String HTTP_510 = "Not Extended";
	public final static String HTTP_511 = "Network Authentication Required";

}
