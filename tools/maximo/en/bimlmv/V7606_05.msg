<?xml version="1.0" encoding="UTF-8"?>
<!--
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
-->

<messages cleanup="false" version="BIM75">
  <Message id="BIMAA0177E" group="bimlmv" key="autodesk_api_err" prefix="NO">
    <Display method="MSGBOX">
      <Option>MSG_BTNCLOSE</Option>
      <Option>MSG_ICONEXCLAMATION</Option>
    </Display>
    <MsgText>Error accessing the Autodesk cloud: {0} - {1}</MsgText>
    <Explanation></Explanation>
    <AdminResponse></AdminResponse>
    <OperatorResponse></OperatorResponse>
  </Message>
  <Message id="BIMAA0178E" group="bimlmv" key="bad_checksum" prefix="NO">
    <Display method="MSGBOX">
      <Option>MSG_BTNCLOSE</Option>
      <Option>MSG_ICONEXCLAMATION</Option>
    </Display>
    <MsgText>Upload file checksum mismatch</MsgText>
    <Explanation>The checksum for the uploaded file reported by the Autodesk service does not match the local file checksum.  Try the upload again.</Explanation>
    <AdminResponse></AdminResponse>
    <OperatorResponse></OperatorResponse>
  </Message>
  <Message id="BIMAA0179E" group="bimlmv" key="No_object" prefix="NO">
    <Display method="MSGBOX">
    </Display>
    <MsgText>No objects created by the upload</MsgText>
    <Explanation></Explanation>
    <AdminResponse></AdminResponse>
    <OperatorResponse>Try the upload again</OperatorResponse>
  </Message>
  <Message id="BIMAA0182E" group="bimlmv" key="network-fault" prefix="YES">
    <Display method="MSGBOX">
      <Option>MSG_BTNCLOSE</Option>
      <Option>MSG_ICONEXCLAMATION</Option>
    </Display>
    <MsgText>&quot;An exception occurred while contacting the Autodesk cloud service&quot;</MsgText>
    <Explanation></Explanation>
    <AdminResponse></AdminResponse>
    <OperatorResponse></OperatorResponse>
  </Message>
  <Message id="BIMAA0183I" group="bimlmv" key="no-delete-during-upload" prefix="NO">
    <Display method="MSGBOX">
      <Option>MSG_BTNCLOSE</Option>
    </Display>
    <MsgText>A model can&apos;t be delete while an upload is in progress</MsgText>
    <Explanation></Explanation>
    <AdminResponse></AdminResponse>
    <OperatorResponse></OperatorResponse>
  </Message>
  <Message id="BIMAA0184I" group="bimlmv" key="TOOLBAR_HIDE_SELECTION" prefix="NO">
    <Display method="STATUS">
    </Display>
    <MsgText>Hide selection</MsgText>
    <Explanation></Explanation>
    <AdminResponse></AdminResponse>
    <OperatorResponse></OperatorResponse>
  </Message>
  <Message id="BIMAA0185I" group="bimlmv" key="TOOLBAR_SEARCH" prefix="NO">
    <Display method="TEXT">
    </Display>
    <MsgText>Search</MsgText>
    <Explanation></Explanation>
    <AdminResponse></AdminResponse>
    <OperatorResponse></OperatorResponse>
  </Message>
  <Message id="BIMAA0186I" group="bimlmv" key="TOOLBAR_SELECTION_MODE" prefix="NO">
    <Display method="TEXT">
    </Display>
    <MsgText>Set selection mode</MsgText>
    <Explanation></Explanation>
    <AdminResponse></AdminResponse>
    <OperatorResponse></OperatorResponse>
  </Message>
  <Message id="BIMAA0187I" group="bimlmv" key="TOOLBAR_ZOOM_MODEL" prefix="NO">
    <Display method="TEXT">
    </Display>
    <MsgText>Zoom to model</MsgText>
    <Explanation></Explanation>
    <AdminResponse></AdminResponse>
    <OperatorResponse></OperatorResponse>
  </Message>
  <Message id="BIMAA0188I" group="bimlmv" key="KEY_OF" prefix="NO">
    <Display method="TEXT">
    </Display>
    <MsgText>of</MsgText>
    <Explanation></Explanation>
    <AdminResponse></AdminResponse>
    <OperatorResponse></OperatorResponse>
  </Message>
  <Message id="BIMAA0189I" group="bimlnv" key="KEY_DASH" prefix="NO">
    <Display method="TEXT">
    </Display>
    <MsgText>-</MsgText>
    <Explanation></Explanation>
    <AdminResponse></AdminResponse>
    <OperatorResponse></OperatorResponse>
  </Message>
  <Message id="BIMAA0190I" group="bimlmv" key="KEY_OPEN_FILTER" prefix="NO">
    <Display method="TEXT">
    </Display>
    <MsgText>Open Filter</MsgText>
    <Explanation></Explanation>
    <AdminResponse></AdminResponse>
    <OperatorResponse></OperatorResponse>
  </Message>
  <Message id="BIMAA0191I" group="bimlmv" key="KEY_CLOSE_FILTER" prefix="NO">
    <Display method="TEXT">
    </Display>
    <MsgText>Close Filter</MsgText>
    <Explanation></Explanation>
    <AdminResponse></AdminResponse>
    <OperatorResponse></OperatorResponse>
  </Message>
  <Message id="BIMAA0192I" group="bimlmv" key="KEY_FILTER_TABLE" prefix="NO">
    <Display method="TEXT">
    </Display>
    <MsgText>Filter Table</MsgText>
    <Explanation></Explanation>
    <AdminResponse></AdminResponse>
    <OperatorResponse></OperatorResponse>
  </Message>
  <Message id="BIMAA0193I" group="bimlmv" key="KEY_PREV_PAGE" prefix="NO">
    <Display method="TEXT">
    </Display>
    <MsgText>Previous Page</MsgText>
    <Explanation></Explanation>
    <AdminResponse></AdminResponse>
    <OperatorResponse></OperatorResponse>
  </Message>
  <Message id="BIMAA0194I" group="bimlmv" key="KEY_NEXT_PAGE" prefix="NO">
    <Display method="TEXT">
    </Display>
    <MsgText>Next Page</MsgText>
    <Explanation></Explanation>
    <AdminResponse></AdminResponse>
    <OperatorResponse></OperatorResponse>
  </Message>
  <Message id="BIMAA0195I" group="bimlmv" key="KEY_CLOSE" prefix="NO">
    <Display method="TEXT">
    </Display>
    <MsgText>Close</MsgText>
    <Explanation></Explanation>
    <AdminResponse></AdminResponse>
    <OperatorResponse></OperatorResponse>
  </Message>
  <Message id="BIMAA0196I" group="bimlmv" key="KEY_ASSET_PROP_TITLE" prefix="NO">
    <Display method="TEXT">
    </Display>
    <MsgText>Asset : {0}</MsgText>
    <Explanation></Explanation>
    <AdminResponse></AdminResponse>
    <OperatorResponse></OperatorResponse>
  </Message>
  <Message id="BIMAA0197I" group="bimlmv" key="KEY_DASH" prefix="NO">
    <Display method="TEXT">
    </Display>
    <MsgText>-</MsgText>
    <Explanation></Explanation>
    <AdminResponse></AdminResponse>
    <OperatorResponse></OperatorResponse>
  </Message>
  <Message id="BIMAA0198W" group="bimlmv" key="uploding-model-file" prefix="NO">
    <Display method="TEXT">
    </Display>
    <MsgText>Uploading model file</MsgText>
    <Explanation></Explanation>
    <AdminResponse></AdminResponse>
    <OperatorResponse></OperatorResponse>
  </Message>
  <Message id="BIMAA0199I" group="bimlmv" key="bytes-to-upload" prefix="NO">
    <Display method="TEXT">
    </Display>
    <MsgText>{0} bytes</MsgText>
    <Explanation></Explanation>
    <AdminResponse></AdminResponse>
    <OperatorResponse></OperatorResponse>
  </Message>
  <Message id="BIMAA0200W" group="bimlmv" key="bucket_not_found" prefix="NO">
    <Display method="MSGBOX">
      <Option>MSG_BTNCLOSE</Option>
      <Option>MSG_ICONWARNING</Option>
    </Display>
    <MsgText>Bucket {0} was not found for user key {1}</MsgText>
    <Explanation></Explanation>
    <AdminResponse></AdminResponse>
    <OperatorResponse></OperatorResponse>
  </Message>
  <Message id="BIMAA0201W" group="bimlmv" key="model_not_found" prefix="NO">
    <Display method="MSGBOX">
      <Option>MSG_BTNCLOSE</Option>
      <Option>MSG_ICONWARNING</Option>
    </Display>
    <MsgText>Model {0} was not found</MsgText>
    <Explanation></Explanation>
    <AdminResponse></AdminResponse>
    <OperatorResponse></OperatorResponse>
  </Message>
  <Message id="BIMAA0202I" group="bimlmv" key="submit-auto-registration" prefix="NO">
    <Display method="TEXT">
      <Option>MSG_BTNCLOSE</Option>
    </Display>
    <MsgText>Model {0} submitted for registration.  Use the Manage Viewables dialog to check its status</MsgText>
    <Explanation></Explanation>
    <AdminResponse></AdminResponse>
    <OperatorResponse></OperatorResponse>
  </Message>
  <Message id="BIMAA0203I" group="bimlmv" key="upload-complete" prefix="NO">
    <Display method="TEXT">
      <Option>MSG_BTNCLOSE</Option>
    </Display>
    <MsgText>Upload of model complete</MsgText>
    <Explanation></Explanation>
    <AdminResponse></AdminResponse>
    <OperatorResponse></OperatorResponse>
  </Message>
  <Message id="BIMAA0204W" group="bimlmv" key="viewable_not_found" prefix="NO">
    <Display method="MSGBOX">
      <Option>MSG_BTNCLOSE</Option>
      <Option>MSG_ICONWARNING</Option>
    </Display>
    <MsgText>Viewable {0} was not found</MsgText>
    <Explanation></Explanation>
    <AdminResponse></AdminResponse>
    <OperatorResponse></OperatorResponse>
  </Message>
  <Message id="BIMAA0205I" group="bimlmv" key="DLG_TT_SAVE_VIEW" prefix="NO">
    <Display method="TEXT">
    </Display>
    <MsgText>Save View...</MsgText>
    <Explanation></Explanation>
    <AdminResponse></AdminResponse>
    <OperatorResponse></OperatorResponse>
  </Message>
  <Message id="BIMAA0206I" group="bimlmv" key="DLG_TT_APPLY_VIEW" prefix="NO">
    <Display method="TEXT">
    </Display>
    <MsgText>Apply Saved View...</MsgText>
    <Explanation></Explanation>
    <AdminResponse></AdminResponse>
    <OperatorResponse></OperatorResponse>
  </Message>
  <Message id="BIMAA0207I" group="bimlmv" key="DLG_LABEL_SAVE_VIEW" prefix="NO">
    <Display method="TEXT">
    </Display>
    <MsgText>Save View</MsgText>
    <Explanation></Explanation>
    <AdminResponse></AdminResponse>
    <OperatorResponse></OperatorResponse>
  </Message>
  <Message id="BIMAA0208I" group="bimlmv" key="DLG_LABEL_APPLY_VIEW" prefix="NO">
    <Display method="TEXT">
    </Display>
    <MsgText>Apply Saved View</MsgText>
    <Explanation></Explanation>
    <AdminResponse></AdminResponse>
    <OperatorResponse></OperatorResponse>
  </Message>
  <Message id="BIMAA0209I" group="bimlmv" key="DLG_BTN_APPLY" prefix="NO">
    <Display method="TEXT">
    </Display>
    <MsgText>Apply</MsgText>
    <Explanation></Explanation>
    <AdminResponse></AdminResponse>
    <OperatorResponse></OperatorResponse>
  </Message>
  <Message id="BIMAA0210I" group="bimlmv" key="DLG_TITLE_SAVE_VIEW" prefix="NO">
    <Display method="TEXT">
    </Display>
    <MsgText>Save Current View</MsgText>
    <Explanation></Explanation>
    <AdminResponse></AdminResponse>
    <OperatorResponse></OperatorResponse>
  </Message>
  <Message id="BIMAA0211I" group="bimlmv" key="DLG_TITLE_APPLY_VIEW" prefix="NO">
    <Display method="TEXT">
    </Display>
    <MsgText>Select View to Restore</MsgText>
    <Explanation></Explanation>
    <AdminResponse></AdminResponse>
    <OperatorResponse></OperatorResponse>
  </Message>
  <Message id="BIMAA0212I" group="bimlmv" key="DLG_BTN_DELETE_VIEW" prefix="NO">
    <Display method="TEXT">
    </Display>
    <MsgText>Delete Saved View</MsgText>
    <Explanation></Explanation>
    <AdminResponse></AdminResponse>
    <OperatorResponse></OperatorResponse>
  </Message>
  <Message id="BIMAA0213W" group="bimlmv" key="MSG_TITLE_REQUIRED_FIELD" prefix="NO">
    <Display method="TEXT">
    </Display>
    <MsgText>Required Field Missing %1</MsgText>
    <Explanation></Explanation>
    <AdminResponse></AdminResponse>
    <OperatorResponse></OperatorResponse>
  </Message>
  <Message id="BIMAA0214I" group="bimlmv" key="DLG_BTN_CREATE" prefix="NO">
    <Display method="TEXT">
    </Display>
    <MsgText>Create</MsgText>
    <Explanation></Explanation>
    <AdminResponse></AdminResponse>
    <OperatorResponse></OperatorResponse>
  </Message>
  <Message id="BIMAA0215I" group="bimlmv" key="DLG_BTN_CANCEL" prefix="NO">
    <Display method="TEXT">
    </Display>
    <MsgText>Cancel</MsgText>
    <Explanation></Explanation>
    <AdminResponse></AdminResponse>
    <OperatorResponse></OperatorResponse>
  </Message>
  <Message id="BIMAA0216I" group="bimlmv" key="DLG_BTN_OK" prefix="NO">
    <Display method="STATUS">
    </Display>
    <MsgText>OK</MsgText>
    <Explanation></Explanation>
    <AdminResponse></AdminResponse>
    <OperatorResponse></OperatorResponse>
  </Message>
  <Message id="BIMAA0217I" group="bimlmv" key="MAP_BTN_MODELLIST" prefix="NO">
    <Display method="TEXT">
    </Display>
    <MsgText>Display Model List</MsgText>
    <Explanation></Explanation>
    <AdminResponse></AdminResponse>
    <OperatorResponse></OperatorResponse>
  </Message>
  <Message id="BIMAA0218I" group="bimlmv" key="DLG_BTN_LOCATE" prefix="NO">
    <Display method="TEXT">
    </Display>
    <MsgText>Locate</MsgText>
    <Explanation></Explanation>
    <AdminResponse></AdminResponse>
    <OperatorResponse></OperatorResponse>
  </Message>
  <Message id="BIMAA0219I" group="bimlmv" key="MSG_NOT_ASSET" prefix="NO">
    <Display method="TEXT">
    </Display>
    <MsgText>The current selection is not a Maximo asset</MsgText>
    <Explanation></Explanation>
    <AdminResponse></AdminResponse>
    <OperatorResponse></OperatorResponse>
  </Message>
  <Message id="BIMAA0220E" group="bimlmv" key="ERR_NO_GUID" prefix="NO">
    <Display method="TEXT">
    </Display>
    <MsgText>The selected item doesn&apos;t have a unique identifier.  It may be necessary to define a binding in the Manage Building Models application</MsgText>
    <Explanation></Explanation>
    <AdminResponse></AdminResponse>
    <OperatorResponse></OperatorResponse>
  </Message>
  <Message id="BIMAA0221I" group="bimlmv" key="DLG_BTN_VIEW" prefix="NO">
    <Display method="TEXT">
    </Display>
    <MsgText>View</MsgText>
    <Explanation></Explanation>
    <AdminResponse></AdminResponse>
    <OperatorResponse></OperatorResponse>
  </Message>
  <Message id="BIMAA0222I" group="bimlmv" key="KEY_LOCATION_PROP_TITLE" prefix="NO">
    <Display method="TEXT">
    </Display>
    <MsgText>Location: {0}</MsgText>
    <Explanation></Explanation>
    <AdminResponse></AdminResponse>
    <OperatorResponse></OperatorResponse>
  </Message>
  <Message id="BIMAA0223I" group="bimlmv" key="CAT_ADDRESS" prefix="NO">
    <Display method="TEXT">
    </Display>
    <MsgText>Address</MsgText>
    <Explanation></Explanation>
    <AdminResponse></AdminResponse>
    <OperatorResponse></OperatorResponse>
  </Message>
  <Message id="BIMAA0224I" group="bimlmv" key="CAT_COST" prefix="NO">
    <Display method="TEXT">
    </Display>
    <MsgText>Costs</MsgText>
    <Explanation></Explanation>
    <AdminResponse></AdminResponse>
    <OperatorResponse></OperatorResponse>
  </Message>
  <Message id="BIMAA0225I" group="bimlmv" key="CAT_BASE" prefix="NO">
    <Display method="TEXT">
    </Display>
    <MsgText>Basic Information</MsgText>
    <Explanation></Explanation>
    <AdminResponse></AdminResponse>
    <OperatorResponse></OperatorResponse>
  </Message>
  <Message id="BIMAA0226I" group="bimlmv" key="CAT_DETAIL" prefix="NO">
    <Display method="TEXT">
    </Display>
    <MsgText>Details</MsgText>
    <Explanation></Explanation>
    <AdminResponse></AdminResponse>
    <OperatorResponse></OperatorResponse>
  </Message>
  <Message id="BIMAA0227I" group="bimlmv" key="CAT_DOWN" prefix="NO">
    <Display method="TEXT">
    </Display>
    <MsgText>Down Time</MsgText>
    <Explanation></Explanation>
    <AdminResponse></AdminResponse>
    <OperatorResponse></OperatorResponse>
  </Message>
  <Message id="BIMAA0228I" group="bimlmv" key="CAT_FACILITY" prefix="NO">
    <Display method="TEXT">
    </Display>
    <MsgText>Facilities</MsgText>
    <Explanation></Explanation>
    <AdminResponse></AdminResponse>
    <OperatorResponse></OperatorResponse>
  </Message>
  <Message id="BIMAA0229I" group="bimlmv" key="CAT_MODIFY" prefix="NO">
    <Display method="TEXT">
    </Display>
    <MsgText>Modified</MsgText>
    <Explanation></Explanation>
    <AdminResponse></AdminResponse>
    <OperatorResponse></OperatorResponse>
  </Message>
  <Message id="BIMAA0230I" group="bimlmv" key="CAT_PURCHASE" prefix="NO">
    <Display method="TEXT">
    </Display>
    <MsgText>Purchase Information</MsgText>
    <Explanation></Explanation>
    <AdminResponse></AdminResponse>
    <OperatorResponse></OperatorResponse>
  </Message>
  <Message id="BIMAA0231I" group="bimlmv" key="CAT_OTHER" prefix="NO">
    <Display method="TEXT">
    </Display>
    <MsgText>Other</MsgText>
    <Explanation></Explanation>
    <AdminResponse></AdminResponse>
    <OperatorResponse></OperatorResponse>
  </Message>
  <Message id="BIMAA0232I" group="bimlmv" key="CAT_SPEC" prefix="NO">
    <Display method="MSGBOX">
    </Display>
    <MsgText>Specifications</MsgText>
    <Explanation></Explanation>
    <AdminResponse></AdminResponse>
    <OperatorResponse></OperatorResponse>
  </Message>
  <Message id="BIMAA0233I" group="bimlmv" key="KEY_PROP_LOADIN_MSG" prefix="NO">
    <Display method="TEXT">
    </Display>
    <MsgText>Loading properties...</MsgText>
    <Explanation></Explanation>
    <AdminResponse></AdminResponse>
    <OperatorResponse></OperatorResponse>
  </Message>
  <Message id="BIMAA0234I" group="bimlmv" key="KEY_ERROR" prefix="NO">
    <Display method="TEXT">
    </Display>
    <MsgText>Error</MsgText>
    <Explanation></Explanation>
    <AdminResponse></AdminResponse>
    <OperatorResponse></OperatorResponse>
  </Message>
  <Message id="BIMAA0235I" group="bimlmv" key="KEY_ATTACHMENT" prefix="NO">
    <Display method="TEXT">
    </Display>
    <MsgText>Attachments for {0}</MsgText>
    <Explanation></Explanation>
    <AdminResponse></AdminResponse>
    <OperatorResponse></OperatorResponse>
  </Message>
  <Message id="BIMAA0236I" group="bimlmv" key="KEY_CLASS_STRUCT" prefix="NO">
    <Display method="TEXT">
    </Display>
    <MsgText>Hierarchy Path</MsgText>
    <Explanation></Explanation>
    <AdminResponse></AdminResponse>
    <OperatorResponse></OperatorResponse>
  </Message>
  <Message id="BIMAA0237I" group="bimlmv" key="KEY_DISABLE_AUTO_ZOOM" prefix="NO">
    <Display method="TEXT">
    </Display>
    <MsgText>Disable Auto Zoom</MsgText>
    <Explanation></Explanation>
    <AdminResponse></AdminResponse>
    <OperatorResponse></OperatorResponse>
  </Message>
  <Message id="BIMAA0238I" group="bimlmv" key="KEY_ENABLE_AUTO_ZOOM" prefix="NO">
    <Display method="TEXT">
    </Display>
    <MsgText>Enable Auto Zoom</MsgText>
    <Explanation></Explanation>
    <AdminResponse></AdminResponse>
    <OperatorResponse></OperatorResponse>
  </Message>
  <Message id="BIMAA0239I" group="bimlmv" key="TOOLBAR_ISOLATE_SELECTION" prefix="NO">
    <Display method="TEXT">
    </Display>
    <MsgText>Isolate Selection</MsgText>
    <Explanation></Explanation>
    <AdminResponse></AdminResponse>
    <OperatorResponse></OperatorResponse>
  </Message>
  <Message id="BIMAA0245E" group="bimlmv" key="Bad Request" prefix="NO">
    <Display method="TEXT">
    </Display>
    <MsgText>{0}: Bad Request</MsgText>
    <Explanation>The server cannot or will not process the request due to an apparent client error (e.g., malformed request syntax, invalid request message framing, or deceptive request routing).</Explanation>
    <AdminResponse></AdminResponse>
    <OperatorResponse></OperatorResponse>
  </Message>
  <Message id="BIMAA0246E" group="bimlmv" key="Unauthorized" prefix="NO">
    <Display method="TEXT">
    </Display>
    <MsgText>{0}: Unauthorized</MsgText>
    <Explanation>Similar to 403 Forbidden, but specifically for use when authentication is required and has failed or has not yet been provided. The response must include a WWW-Authenticate header field containing a challenge applicable to the requested resource. See Basic access authentication and Digest access authentication.[37] 401 semantically means &quot;unauthenticated&quot;,[38] i.e. the user does not have the necessary credentials.Note: Some sites issue HTTP 401 when an IP address is banned from the website (usually the website domain) and that specific address is refused permission to access a website.</Explanation>
    <AdminResponse></AdminResponse>
    <OperatorResponse></OperatorResponse>
  </Message>
  <Message id="BIMAA0247E" group="bimlmv" key="Payment Required" prefix="NO">
    <Display method="TEXT">
    </Display>
    <MsgText>{0}: Payment Required</MsgText>
    <Explanation>Reserved for future use. The original intention was that this code might be used as part of some form of digital cash or micropayment scheme, but that has not happened, and this code is not usually used.</Explanation>
    <AdminResponse></AdminResponse>
    <OperatorResponse></OperatorResponse>
  </Message>
  <Message id="BIMAA0248E" group="bimlmv" key="Forbidden" prefix="NO">
    <Display method="TEXT">
    </Display>
    <MsgText>{0}: Forbidden</MsgText>
    <Explanation>The request was a valid request, but the server is refusing to respond to it. 403 error semantically means &quot;unauthorized&quot;, i.e. the user does not have the necessary permissions for the resource</Explanation>
    <AdminResponse></AdminResponse>
    <OperatorResponse></OperatorResponse>
  </Message>
  <Message id="BIMAA0249E" group="bimlmv" key="Not Found" prefix="NO">
    <Display method="TEXT">
    </Display>
    <MsgText>{0}: Not Found</MsgText>
    <Explanation>The requested resource could not be found but may be available in the future. Subsequent requests by the client are permissible</Explanation>
    <AdminResponse></AdminResponse>
    <OperatorResponse></OperatorResponse>
  </Message>
  <Message id="BIMAA0250E" group="bimlmv" key="Method Not Allowed" prefix="NO">
    <Display method="TEXT">
    </Display>
    <MsgText>{0}: Method Not Allowed</MsgText>
    <Explanation>A request method is not supported for the requested resource; for example, a GET request on a form which requires data to be presented via POST, or a PUT request on a read-only resource.</Explanation>
    <AdminResponse></AdminResponse>
    <OperatorResponse></OperatorResponse>
  </Message>
  <Message id="BIMAA0251E" group="bimlmv" key="Not Acceptable" prefix="YES">
    <Display method="TEXT">
    </Display>
    <MsgText>{0}; Not Acceptable</MsgText>
    <Explanation>The requested resource is capable of generating only content not acceptable according to the Accept headers sent in the request.[</Explanation>
    <AdminResponse></AdminResponse>
    <OperatorResponse></OperatorResponse>
  </Message>
  <Message id="BIMAA0252E" group="bimlmv" key="Proxy Authentication Required" prefix="NO">
    <Display method="TEXT">
    </Display>
    <MsgText>{0}: Proxy Authentication Required</MsgText>
    <Explanation>The client must first authenticate itself with the proxy.</Explanation>
    <AdminResponse></AdminResponse>
    <OperatorResponse></OperatorResponse>
  </Message>
  <Message id="BIMAA0253E" group="bimlmv" key="Request Timeout" prefix="NO">
    <Display method="TEXT">
    </Display>
    <MsgText>{0}: Request Timeout</MsgText>
    <Explanation>The server timed out waiting for the request. According to HTTP specifications: &quot;The client did not produce a request within the time that the server was prepared to wait. The client MAY repeat the request without modifications at any later time.&quot;[</Explanation>
    <AdminResponse></AdminResponse>
    <OperatorResponse></OperatorResponse>
  </Message>
  <Message id="BIMAA0254E" group="bimlmv" key="Conflict" prefix="NO">
    <Display method="TEXT">
    </Display>
    <MsgText>{0}: Conflict</MsgText>
    <Explanation>Indicates that the request could not be processed because of conflict in the request, such as an edit conflict between multiple simultaneous updates.</Explanation>
    <AdminResponse></AdminResponse>
    <OperatorResponse></OperatorResponse>
  </Message>
  <Message id="BIMAA0255E" group="bimlmv" key="Gone" prefix="NO">
    <Display method="TEXT">
    </Display>
    <MsgText>{0}: Gone</MsgText>
    <Explanation>Indicates that the resource requested is no longer available and will not be available again. This should be used when a resource has been intentionally removed and the resource should be purged. Upon receiving a 410 status code, the client should not request the resource in the future. Clients such as search engines should remove the resource from their indices.[44] Most use cases do not require clients and search engines to purge the resource, and a &quot;404 Not Found&quot; may be used instead.</Explanation>
    <AdminResponse></AdminResponse>
    <OperatorResponse></OperatorResponse>
  </Message>
  <Message id="BIMAA0256E" group="bimlmv" key="Length Required" prefix="NO">
    <Display method="TEXT">
    </Display>
    <MsgText>{0}: Length Required</MsgText>
    <Explanation>The request did not specify the length of its content, which is required by the requested resource.</Explanation>
    <AdminResponse></AdminResponse>
    <OperatorResponse></OperatorResponse>
  </Message>
  <Message id="BIMAA0257E" group="bimlmv" key="Precondition Failed" prefix="NO">
    <Display method="TEXT">
    </Display>
    <MsgText>{0}: Precondition Failed</MsgText>
    <Explanation>The server does not meet one of the preconditions that the requester put on the request.[</Explanation>
    <AdminResponse></AdminResponse>
    <OperatorResponse></OperatorResponse>
  </Message>
  <Message id="BIMAA0258E" group="bimlmv" key="Payload Too Large" prefix="NO">
    <Display method="TEXT">
    </Display>
    <MsgText>{0}: Payload Too Large</MsgText>
    <Explanation>The request is larger than the server is willing or able to process. Previously called &quot;Request Entity Too Large&quot;.[</Explanation>
    <AdminResponse></AdminResponse>
    <OperatorResponse></OperatorResponse>
  </Message>
  <Message id="BIMAA0259E" group="bimlmv" key="URI Too Long" prefix="NO">
    <Display method="TEXT">
    </Display>
    <MsgText>{0}: URI Too Long</MsgText>
    <Explanation>The URI provided was too long for the server to process. Often the result of too much data being encoded as a query-string of a GET request, in which case it should be converted to a POST request.[48] Called &quot;Request-URI Too Long&quot; previously.[</Explanation>
    <AdminResponse></AdminResponse>
    <OperatorResponse></OperatorResponse>
  </Message>
  <Message id="BIMAA0260E" group="bimlmv" key="Unsupported Media Type" prefix="NO">
    <Display method="TEXT">
    </Display>
    <MsgText>{0}: Unsupported Media Type</MsgText>
    <Explanation>The request entity has a media type which the server or resource does not support. For example, the client uploads an image as image/svg+xml, but the server requires that images use a different format.</Explanation>
    <AdminResponse></AdminResponse>
    <OperatorResponse></OperatorResponse>
  </Message>
  <Message id="BIMAA0261E" group="bimlmv" key="Expectation Failed" prefix="NO">
    <Display method="TEXT">
    </Display>
    <MsgText>Expectation Failed</MsgText>
    <Explanation>The server cannot meet the requirements of the Expect request-header field.[</Explanation>
    <AdminResponse></AdminResponse>
    <OperatorResponse></OperatorResponse>
  </Message>
  <Message id="BIMAA0262E" group="bimlmv" key="Range Not Satisfiable" prefix="NO">
    <Display method="TEXT">
    </Display>
    <MsgText>{0}: Range Not Satisfiable</MsgText>
    <Explanation>The client has asked for a portion of the file (byte serving), but the server cannot supply that portion. For example, if the client asked for a part of the file that lies beyond the end of the file.[50] Called &quot;Requested Range Not Satisfiable&quot; previously.</Explanation>
    <AdminResponse></AdminResponse>
    <OperatorResponse></OperatorResponse>
  </Message>
  <Message id="BIMAA0263E" group="bimlmv" key="Misdirected Request" prefix="NO">
    <Display method="TEXT">
    </Display>
    <MsgText>{0}: Misdirected Request</MsgText>
    <Explanation>The request was directed at a server that is not able to produce a response (for example because a connection reuse).</Explanation>
    <AdminResponse></AdminResponse>
    <OperatorResponse></OperatorResponse>
  </Message>
  <Message id="BIMAA0264E" group="bimlmv" key="Unprocessable Entity" prefix="NO">
    <Display method="TEXT">
    </Display>
    <MsgText>{0}: Unprocessable Entity</MsgText>
    <Explanation>The request was well-formed but was unable to be followed due to semantic errors.[</Explanation>
    <AdminResponse></AdminResponse>
    <OperatorResponse></OperatorResponse>
  </Message>
  <Message id="BIMAA0265E" group="bimlmv" key="Locked" prefix="NO">
    <Display method="TEXT">
    </Display>
    <MsgText>{0}: Locked</MsgText>
    <Explanation>The resource that is being accessed is locked.[</Explanation>
    <AdminResponse></AdminResponse>
    <OperatorResponse></OperatorResponse>
  </Message>
  <Message id="BIMAA0266E" group="bimlmv" key="Upgrade Required" prefix="NO">
    <Display method="TEXT">
    </Display>
    <MsgText>{0}: Upgrade Required</MsgText>
    <Explanation>The client should switch to a different protocol such as TLS/1.0, given in the Upgrade header field.</Explanation>
    <AdminResponse></AdminResponse>
    <OperatorResponse></OperatorResponse>
  </Message>
  <Message id="BIMAA0267E" group="bimlmv" key="Failed Dependency" prefix="NO">
    <Display method="TEXT">
    </Display>
    <MsgText>{0}: Failed Dependency</MsgText>
    <Explanation>The request failed due to failure of a previous request (e.g., a PROPPATCH).[</Explanation>
    <AdminResponse></AdminResponse>
    <OperatorResponse></OperatorResponse>
  </Message>
  <Message id="BIMAA0268E" group="bimlmv" key="Request Header Fields Too Large" prefix="NO">
    <Display method="TEXT">
    </Display>
    <MsgText>{0}: Request Header Fields Too Large</MsgText>
    <Explanation>The server is unwilling to process the request because either an individual header field, or all the header fields collectively, are too large.[</Explanation>
    <AdminResponse></AdminResponse>
    <OperatorResponse></OperatorResponse>
  </Message>
  <Message id="BIMAA0269E" group="bimlmv" key="Precondition Required" prefix="NO">
    <Display method="TEXT">
    </Display>
    <MsgText>{0}: Precondition Required</MsgText>
    <Explanation>The origin server requires the request to be conditional. Intended to prevent &quot;the &apos;lost update&apos; problem, where a client GETs a resource&apos;s state, modifies it, and PUTs it back to the server, when meanwhile a third party has modified the state on the server, leading to a conflict.&quot;[</Explanation>
    <AdminResponse></AdminResponse>
    <OperatorResponse></OperatorResponse>
  </Message>
  <Message id="BIMAA0270E" group="bimlmv" key="Too Many Requests" prefix="NO">
    <Display method="TEXT">
    </Display>
    <MsgText>{0}: Too Many Requests</MsgText>
    <Explanation>The user has sent too many requests in a given amount of time. Intended for use with rate limiting schemes.[</Explanation>
    <AdminResponse></AdminResponse>
    <OperatorResponse></OperatorResponse>
  </Message>
  <Message id="BIMAA0271E" group="bimlmv" key="Unavailable For Legal Reasons" prefix="NO">
    <Display method="TEXT">
    </Display>
    <MsgText>{0}: Unavailable For Legal Reasons</MsgText>
    <Explanation>A server operator has received a legal demand to deny access to a resource or to a set of resources that includes the requested resource.[58] The code 451 was chosen as a reference to the novel Fahrenheit 451</Explanation>
    <AdminResponse></AdminResponse>
    <OperatorResponse></OperatorResponse>
  </Message>
  <Message id="BIMAA0272E" group="bimlmv" key="Internal Server Error" prefix="NO">
    <Display method="TEXT">
    </Display>
    <MsgText>{0}: Internal Server Error</MsgText>
    <Explanation>A generic error message, given when an unexpected condition was encountered and no more specific message is suitable.[</Explanation>
    <AdminResponse></AdminResponse>
    <OperatorResponse></OperatorResponse>
  </Message>
  <Message id="BIMAA0273E" group="bimlmv" key="Not Implemented" prefix="NO">
    <Display method="TEXT">
    </Display>
    <MsgText>{0}: Not Implemented</MsgText>
    <Explanation>The server either does not recognize the request method, or it lacks the ability to fulfill the request. Usually this implies future availability (e.g., a new feature of a web-service API).</Explanation>
    <AdminResponse></AdminResponse>
    <OperatorResponse></OperatorResponse>
  </Message>
  <Message id="BIMAA0274E" group="bimlmv" key="Bad Gateway" prefix="NO">
    <Display method="TEXT">
    </Display>
    <MsgText>{0}: Bad Gateway</MsgText>
    <Explanation></Explanation>
    <AdminResponse></AdminResponse>
    <OperatorResponse></OperatorResponse>
  </Message>
  <Message id="BIMAA0275E" group="bimlmv" key="Service Unavailable" prefix="NO">
    <Display method="TEXT">
    </Display>
    <MsgText>{0}: Service Unavailable</MsgText>
    <Explanation>The server is currently unavailable (because it is overloaded or down for maintenance). Generally, this is a temporary state.</Explanation>
    <AdminResponse></AdminResponse>
    <OperatorResponse></OperatorResponse>
  </Message>
  <Message id="BIMAA0276E" group="bimlmv" key="Gateway Timeout" prefix="NO">
    <Display method="TEXT">
    </Display>
    <MsgText>{0}: Gateway Timeout</MsgText>
    <Explanation>The server was acting as a gateway or proxy and did not receive a timely response from the upstream server.[</Explanation>
    <AdminResponse></AdminResponse>
    <OperatorResponse></OperatorResponse>
  </Message>
  <Message id="BIMAA0277E" group="bimlmv" key="HTTP Version Not Supported" prefix="NO">
    <Display method="TEXT">
    </Display>
    <MsgText>{0}: HTTP Version Not Supported</MsgText>
    <Explanation>The server does not support the HTTP protocol version used in the request.</Explanation>
    <AdminResponse></AdminResponse>
    <OperatorResponse></OperatorResponse>
  </Message>
  <Message id="BIMAA0278E" group="bimlmv" key="Variant Also Negotiates" prefix="NO">
    <Display method="TEXT">
    </Display>
    <MsgText>{0}: Variant Also Negotiates</MsgText>
    <Explanation>Transparent content negotiation for the request results in a circular reference.[</Explanation>
    <AdminResponse></AdminResponse>
    <OperatorResponse></OperatorResponse>
  </Message>
  <Message id="BIMAA0279E" group="bimlmv" key="Insufficient Storage" prefix="NO">
    <Display method="TEXT">
    </Display>
    <MsgText>{0}: Insufficient Storage</MsgText>
    <Explanation>The server is unable to store the representation needed to complete the request.[</Explanation>
    <AdminResponse></AdminResponse>
    <OperatorResponse></OperatorResponse>
  </Message>
  <Message id="BIMAA0280E" group="bimlmv" key="Loop Detected" prefix="NO">
    <Display method="TEXT">
    </Display>
    <MsgText>{0} Loop Detected</MsgText>
    <Explanation>The server detected an infinite loop while processing the request (sent in lieu of 208 Already Reported).</Explanation>
    <AdminResponse></AdminResponse>
    <OperatorResponse></OperatorResponse>
  </Message>
  <Message id="BIMAA0281E" group="bimlmv" key="Not Extended" prefix="NO">
    <Display method="TEXT">
    </Display>
    <MsgText>{0}: Not Extended</MsgText>
    <Explanation>Further extensions to the request are required for the server to fulfil it.[</Explanation>
    <AdminResponse></AdminResponse>
    <OperatorResponse></OperatorResponse>
  </Message>
  <Message id="BIMAA0282E" group="bimlmv" key="Network Authentication Required" prefix="NO">
    <Display method="TEXT">
    </Display>
    <MsgText>{0}: Network Authentication Required</MsgText>
    <Explanation>The client needs to authenticate to gain network access. Intended for use by intercepting proxies used to control access to the network (e.g., &quot;captive portals&quot; used to require agreement to Terms of Service before granting full Internet access via a Wi-Fi hotspot).[</Explanation>
    <AdminResponse></AdminResponse>
    <OperatorResponse></OperatorResponse>
  </Message>
  <Message id="BIMAA0297E" group="bimlmv" key="ERR_REST" prefix="NO">
    <Display method="TEXT">
    </Display>
    <MsgText>REST Error</MsgText>
    <Explanation></Explanation>
    <AdminResponse></AdminResponse>
    <OperatorResponse></OperatorResponse>
  </Message>
  <Message id="BIMAA0298I" group="bimlmv" key="KEY_PARENT" prefix="NO">
    <Display method="TEXT">
    </Display>
    <MsgText>Parent</MsgText>
    <Explanation></Explanation>
    <AdminResponse></AdminResponse>
    <OperatorResponse></OperatorResponse>
  </Message>
  <Message id="BIMAA0299I" group="bimlmv" key="KEY_WO_PROP_TITLE" prefix="NO">
    <Display method="TEXT">
    </Display>
    <MsgText>Work Order: {0}</MsgText>
    <Explanation></Explanation>
    <AdminResponse></AdminResponse>
    <OperatorResponse></OperatorResponse>
  </Message>
  <Message id="BIMAA0300I" group="bimlmv" key="KEY_COMPANY_PROP_TITLE" prefix="NO">
    <Display method="TEXT">
    </Display>
    <MsgText>Company: {0}</MsgText>
    <Explanation></Explanation>
    <AdminResponse></AdminResponse>
    <OperatorResponse></OperatorResponse>
  </Message>
  <Message id="BIMAA0301I" group="bimlmv" key="KEY_PRODUCT_PROP_TITLE" prefix="NO">
    <Display method="TEXT">
    </Display>
    <MsgText>Product: {0}</MsgText>
    <Explanation></Explanation>
    <AdminResponse></AdminResponse>
    <OperatorResponse></OperatorResponse>
  </Message>
  <Message id="BIMAA0302I" group="bimlmv" key="DLG_TXT_DELETE_VIEW" prefix="NO">
    <Display method="TEXT">
    </Display>
    <MsgText>Delete saved view</MsgText>
    <Explanation></Explanation>
    <AdminResponse></AdminResponse>
    <OperatorResponse></OperatorResponse>
  </Message>
</messages>
