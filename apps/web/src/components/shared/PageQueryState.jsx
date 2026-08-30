import LoadingSpinner from '@/components/ui/LoadingSpinner';
import ErrorMessage from '@/components/ui/ErrorMessage';
import EmptyState from '@/components/ui/EmptyState';

export default function PageQueryState({
  isLoading,
  isError,
  error,
  isEmpty,
  emptyProps,
  onRetry,
  loadingLabel = 'Loading...',
  children,
}) {
  if (isLoading) {
    return <LoadingSpinner size="lg" label={loadingLabel} />;
  }

  if (isError) {
    return (
      <ErrorMessage
        message={error?.message || 'Something went wrong. Please try again.'}
        onRetry={onRetry}
      />
    );
  }

  if (isEmpty) {
    return <EmptyState {...emptyProps} />;
  }

  return children;
}
