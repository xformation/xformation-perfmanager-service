const path = require('path');

module.exports = {
  // Make sure that this is the correct path to the web interface part of the Perfmanager server repository.
  web_src_path: path.resolve(__dirname, '${serverCheckoutPath}', 'perfmanager-web-interface'),
};
