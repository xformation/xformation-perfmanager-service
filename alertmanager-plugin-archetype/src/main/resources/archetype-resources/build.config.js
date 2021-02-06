const path = require('path');

module.exports = {
  // Make sure that this is the correct path to the web interface part of the alertmanager server repository.
  web_src_path: path.resolve(__dirname, '${serverCheckoutPath}', 'alertmanager-web-interface'),
};
