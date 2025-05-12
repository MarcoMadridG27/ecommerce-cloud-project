interface Props {
    totalPages: number;
    currentPage: number;
    onPageChange: (page: number) => void;
}

const Pagination = ({ totalPages, currentPage, onPageChange }: Props) => {
    const handlePageChange = (page: number) => {
        onPageChange(page);
        window.scrollTo({ top: 0, behavior: "smooth" });
    };

    const createPageButtons = () => {
        const buttons = [];
        const maxVisible = 5;

        if (totalPages <= maxVisible) {
            for (let i = 1; i <= totalPages; i++) {
                buttons.push(i);
            }
        } else {
            buttons.push(1);
            if (currentPage > 3) buttons.push(-1); // -1 represents ellipsis

            const start = Math.max(2, currentPage - 1);
            const end = Math.min(totalPages - 1, currentPage + 1);

            for (let i = start; i <= end; i++) {
                buttons.push(i);
            }

            if (currentPage < totalPages - 2) buttons.push(-1);
            buttons.push(totalPages);
        }

        return buttons;
    };

    const pages = createPageButtons();

    return (
        <div className="join">
            {/* Prev */}
            <button
                className="join-item btn"
                disabled={currentPage === 1}
                onClick={() => handlePageChange(currentPage - 1)}
            >
                «
            </button>

            {/* Page Numbers */}
            {pages.map((page, i) =>
                page === -1 ? (
                    <span key={`ellipsis-${i}`} className="join-item btn btn-disabled">…</span>
                ) : (
                    <button
                        key={page}
                        className={`join-item btn ${currentPage === page ? "btn-primary" : ""}`}
                        onClick={() => handlePageChange(page)}
                    >
                        {page}
                    </button>
                )
            )}

            {/* Next */}
            <button
                className="join-item btn"
                disabled={currentPage === totalPages}
                onClick={() => handlePageChange(currentPage + 1)}
            >
                »
            </button>
        </div>
    );
};

export default Pagination;
