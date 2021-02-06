
import PropTypes from 'prop-types';
import React, { useState } from 'react';
import styled, { createGlobalStyle } from 'styled-components';

import { Button, Modal, Well } from 'components/graylog';
import { Icon } from 'components/common';
import DocumentTitle from 'components/common/DocumentTitle';
import authStyles from 'theme/styles/authStyles';
import { qualifyUrl } from 'util/URLUtils';

const StyledIcon = styled(Icon)`
  margin-left: 6px;
`;

const ServerUnavailableStyles = createGlobalStyle`
  ${authStyles}
`;

const ServerUnavailablePage = ({ server }) => {
  const [showDetails, setShowDetails] = useState(false);

  const _toggleDetails = () => setShowDetails(!showDetails);

  const _formatErrorMessage = () => {
    if (!showDetails) {
      return null;
    }

    const noInformationMessage = (
      <div>
        <hr />
        <p>There is no information available.</p>
      </div>
    );

    if (!server?.error) {
      return noInformationMessage;
    }

    const { error } = server;

    const errorDetails = [];

    if (error.message) {
      errorDetails.push(<dt key="error-title">Error message</dt>, <dd key="error-desc">{error.message}</dd>);
    }

    if (error.originalError) {
      const { originalError } = error;

      errorDetails.push(
        <dt key="status-original-request-title">Original Request</dt>,
        <dd key="status-original-request-content">{String(originalError.method)} {String(originalError.url)}</dd>,
      );

      errorDetails.push(
        <dt key="status-code-title">Status code</dt>,
        <dd key="status-code-desc">{String(originalError.status)}</dd>,
      );

      if (typeof originalError.toString === 'function') {
        errorDetails.push(
          <dt key="full-error-title">Full error message</dt>,
          <dd key="full-error-desc">{originalError.toString()}</dd>,
        );
      }
    }

    if (errorDetails.length === 0) {
      return noInformationMessage;
    }

    return (
      <div>
        <hr style={{ marginTop: 10, marginBottom: 10 }} />
        <p>This is the last response we received from the server:</p>
        <Well bsSize="small" style={{ whiteSpace: 'pre-line' }}>
          <dl style={{ marginBottom: 0 }}>
            {errorDetails}
          </dl>
        </Well>
      </div>
    );
  };

  return (
    <DocumentTitle title="Server unavailable">
      <ServerUnavailableStyles />
      <Modal show>
        <Modal.Header>
          <Modal.Title><Icon name="exclamation-triangle" /> Server currently unavailable</Modal.Title>
        </Modal.Header>
        <Modal.Body>
          <div>
            <p>
              We are experiencing problems connecting to the AlertManager server running on <i>{qualifyUrl('')}</i>.
              Please verify that the server is healthy and working correctly.
            </p>
            <p>You will be automatically redirected to the previous page once we can connect to the server.</p>
            <div>
              <Button bsStyle="primary"
                      tabIndex={0}
                      onClick={_toggleDetails}
                      bsSize="sm">
                {showDetails ? 'Less details' : 'More details'}
                <StyledIcon name={showDetails ? 'chevron-up' : 'chevron-down'} />
              </Button>
              {_formatErrorMessage()}
            </div>
          </div>
        </Modal.Body>
      </Modal>
    </DocumentTitle>
  );
};

ServerUnavailablePage.propTypes = {
  server: PropTypes.object,
};

ServerUnavailablePage.defaultProps = {
  server: undefined,
};

export default ServerUnavailablePage;